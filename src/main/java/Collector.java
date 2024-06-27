import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

public class Collector {

    private static final String SERVER_URL = "http://localhost:8088/collect_data";

    public static void main(String[] args) {
        while (true) {
            try {
                double cpuUsage = getCpuUsage();
                double memoryUsage = getMemoryUsage();
                int pcId=1;
                sendData(pcId,cpuUsage, memoryUsage);
                Thread.sleep(60000); // 每5秒钟采集一次数据
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static double getCpuUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        return osBean.getSystemCpuLoad() * 100;
    }

    private static double getMemoryUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        return (osBean.getTotalPhysicalMemorySize() - osBean.getFreePhysicalMemorySize()) * 100.0 / osBean.getTotalPhysicalMemorySize();
    }

    private static void sendData(int pcId, double cpuUsage, double memoryUsage) throws IOException {
        String data = String.format("{\"pc_id\":%d, \"cpu_usage\":%.2f, \"memory_usage\":%.2f}", pcId, cpuUsage, memoryUsage);
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        URL url = new URL(SERVER_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.getOutputStream().write(out);
        System.out.println(data);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Data sent successfully");

        } else {
            System.out.println("Failed to send data");
        }

        connection.disconnect();
    }
}
