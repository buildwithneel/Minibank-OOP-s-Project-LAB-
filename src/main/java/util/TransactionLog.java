package util;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.APPEND;
public final class TransactionLog {
    private TransactionLog() {}
    public static void append(Path file, String line) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.write(file, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8), CREATE, APPEND);
    }
}
