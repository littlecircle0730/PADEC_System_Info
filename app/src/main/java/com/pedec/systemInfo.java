package com.pedec;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class systemInfo implements GLSurfaceView.Renderer{
    private static StringBuilder sb;
    private static int sLastCpuCoreCount = -1;
    public String TAG="PADEC System Info";

    static String getCPUInfo() {
//        return String.format("Total CPU Usage: %s\n", new CPUInfo().getTotalCpuUsage());

        StringBuffer sb = new StringBuffer();
        sb.append("abi: ").append(Build.CPU_ABI).append("\n");
        if (new File("/proc/cpuinfo").exists()) {
            try {
                BufferedReader br = new BufferedReader(
                        new FileReader(new File("/proc/cpuinfo")));
                String aLine;
                while ((aLine = br.readLine()) != null) {
                    sb.append(aLine + "\n");
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String coreInfo = "";
        for (int i = 0; i < calcCpuCoreCount(); i++) {
            coreInfo = coreInfo + takeCurrentCpuFreq(i) +", ";
        }

        return sb.toString() + "Current Cpu Freq of each Core:" + coreInfo;
    }

    private static int readIntegerFile(String filePath) {

        try {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath)), 1000);
            final String line = reader.readLine();
            reader.close();

            return Integer.parseInt(line);
        } catch (Exception e) {
            return 0;
        }
    }

    private static int takeCurrentCpuFreq(int coreIndex) {
        return readIntegerFile("/sys/devices/system/cpu/cpu" + coreIndex + "/cpufreq/scaling_cur_freq");
    }

    public static int calcCpuCoreCount() {

        if (sLastCpuCoreCount >= 1) {
            return sLastCpuCoreCount;
        }

        try {
            // Get directory containing CPU info
            final File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            final File[] files = dir.listFiles(new FileFilter() {

                public boolean accept(File pathname) {
                    //Check if filename is "cpu", followed by a single digit number
                    if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                        return true;
                    }
                    return false;
                }
            });

            // Return the number of cores (virtual CPU devices)
            sLastCpuCoreCount = files.length;

        } catch(Exception e) {
            sLastCpuCoreCount = Runtime.getRuntime().availableProcessors();
        }

        return sLastCpuCoreCount;
    }

    static String getGPUInfo(ActivityManager actManager) {
        final ConfigurationInfo configurationInfo = actManager
                .getDeviceConfigurationInfo();
        sb.append("GL version:").append(configurationInfo.getGlEsVersion()).append("\n");
        return sb.toString();
    }

    static String getRAMInfo(ActivityManager actManager) {
        String availRAM;
        String totalRAM;

        // Declaring MemoryInfo object
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        // Fetching the data from the ActivityManager
        actManager.getMemoryInfo(memoryInfo);

        // Fetching the available and total memory and converting into Giga Bytes
        double availMemory = Double.valueOf(memoryInfo.availMem);
        double totalMemory= Double.valueOf(memoryInfo.totalMem);

        if(availMemory <= 0) availRAM = "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(availMemory)/Math.log10(1024));

        if(totalMemory <= 0) totalRAM = "0";
        int digitGroupsTotal = (int) (Math.log10(totalMemory)/Math.log10(1024));

        availRAM = String.format("Available RAM: %s\n", new DecimalFormat("#,##0.###").format(availMemory/Math.pow(1024, digitGroups)) + " " + units[digitGroups]);
        totalRAM = String.format("Total RAM: %s\n", new DecimalFormat("#,##0.###").format(totalMemory/Math.pow(1024, digitGroupsTotal)) + " " + units[digitGroupsTotal]);

        return availRAM+totalRAM;
    }

    static String getPowerInfo(Intent batteryStatus) {
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        //Determine the current battery level
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level * 100 / (float)scale;

        return String.format("Is Charging: %s\nCharging by: %s\nCurrent Battery: %s%%\n", isCharging, usbCharge?"USB":"AC Power", batteryPct);
    }

    static String getStorageInfo() {
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
            availableSpace = (long) stat.getBlockSizeLong() * (long) stat.getAvailableBlocksLong();
        else
            availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();

        if(availableSpace <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(availableSpace)/Math.log10(1024));
        return String.format("Available Storage Space %s\n", new DecimalFormat("#,##0.###").format(availableSpace/Math.pow(1024, digitGroups)) + " " + units[digitGroups]);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        sb.append("RENDERER").append(gl.glGetString(GL10.GL_RENDERER)).append("\n");
//        sb.append("VENDOR").append( gl.glGetString(GL10.GL_VENDOR)).append("\n");
//        sb.append("VERSION").append(gl.glGetString(GL10.GL_VERSION)).append("\n");
//        sb.append("EXTENSIONS").append(gl.glGetString(GL10.GL_EXTENSIONS));
////        runOnUiThread(new Runnable() {
////            @Override
////            public void run() {
////                textView.setText(sb.toString());
////                glSurfaceView.setVisibility(View.GONE);
////            }
////        });
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
