package util;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
public final class ReportGenerator {
    private ReportGenerator() {}
    public static String generate(Path logsFolder, Path reportFile) throws IOException {
        long deposits = 0;
        long withdrawals = 0;
        StringBuilder details = new StringBuilder();
        if (Files.exists(logsFolder)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsFolder)) {
                for (Path file : stream) {
                    if (!Files.isRegularFile(file)) continue;
                    BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                    details.append(file.getFileName()).append(" size=").append(attrs.size()).append(" lastModified=").append(attrs.lastModifiedTime()).append('\n');
                    try (BufferedReader reader = Files.newBufferedReader(file)) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.trim().split("\\s+");
                            if (parts.length >= 2) {
                                try {
                                    long amount = Long.parseLong(parts[1]);
                                    if (parts[0].equals("DEPOSIT")) deposits += amount;
                                    if (parts[0].equals("WITHDRAW")) withdrawals += amount;
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                    }
                }
            }
        }
        String report = "MINIBANK END-OF-DAY REPORT\nTotal deposits: " + deposits + "\nTotal withdrawals: " + withdrawals + "\nFile details:\n" + details;
        Path parent = reportFile.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.writeString(reportFile, report, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return report;
    }
}
