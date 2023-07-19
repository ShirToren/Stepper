package step.impl;

import dd.impl.DataDefinitionRegistry;
import flow.execution.context.StepExecutionContext;
import logs.LogLine;
import step.api.AbstractStepDefinition;
import step.api.DataDefinitionDeclarationImpl;
import step.api.DataNecessity;
import step.api.StepResult;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;



public class ZipperStep extends AbstractStepDefinition {

    public ZipperStep() {
        super("Zipper", false);
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("OPERATION", DataNecessity.MANDATORY, "Operation type", DataDefinitionRegistry.ENUMERATION));
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Zip operation result", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        context.storeExecutedStep();
        Instant start = Instant.now();
        LocalTime startTime = LocalTime.now();
        context.storeStartTime(startTime);
        String operation = context.getDataValue("OPERATION", String.class);
        String source = context.getDataValue("SOURCE", String.class);
        StepResult result = StepResult.SUCCESS;
        String zipFilePath = "";

        if(operation.equals("ZIP")) {
            try {
                Path folderPath = Paths.get(source);
                if (Files.exists(folderPath)) {
                    if (Files.isDirectory(folderPath)) {
                        zipFilePath = source.concat(".zip");
                    } else {
                        if (source.contains(".")) {
                            zipFilePath = source.substring(0, source.lastIndexOf(".")).concat(".zip");
                        } else {
                            zipFilePath = source.concat(".zip");
                        }
                    }

                    try (FileOutputStream fos = new FileOutputStream(zipFilePath);
                         ZipOutputStream zos = new ZipOutputStream(fos)) {
                        context.addLogLine(new LogLine("About to perform operation " + operation + " on source " + source, LocalTime.now()));
                        if (Files.isDirectory(folderPath)) {
                            zipDirectory(source, zos);
                        } else {
                            zipFile(source, zos);
                        }
                        context.addSummeryLine("Step result is Success!");
                        result = StepResult.SUCCESS;
                        context.storeDataValue("RESULT", "Success");
                    } catch (IOException e) {
                        e.printStackTrace();
                        result = StepResult.FAILURE;
                        context.storeDataValue("RESULT", "Failure. Error while trying to perform operation");
                        context.addSummeryLine("Step is failure. Error while trying to perform operation.");
                        context.addLogLine(new LogLine("Error while trying to perform operation.", LocalTime.now()));
                    }
                } else {
                    result = StepResult.FAILURE;
                    context.storeDataValue("RESULT", "Failure. Source file path doesn't exist");
                    context.addSummeryLine("Step is failure. Source file path doesn't exist.");
                    context.addLogLine(new LogLine("Source file path doesn't exist.", LocalTime.now()));
                }
            } catch (InvalidPathException ex) {
                result = StepResult.FAILURE;
                context.storeDataValue("RESULT", "Failure. Error while trying to perform operation");
                context.addSummeryLine("Step is failure. Error while trying to perform operation.");
                context.addLogLine(new LogLine("Error while trying to perform operation.", LocalTime.now()));
            }
        } else if(operation.equals("UNZIP")) {
            byte[] buffer = new byte[2048];
            try {
                Path outDir = Paths.get(source.substring(0, source.lastIndexOf(".") ));
                String zipFileName = source;

                context.addLogLine(new LogLine("About to perform operation " + operation + " on source " + source, LocalTime.now()));
                try (FileInputStream fis = new FileInputStream(zipFileName);
                     BufferedInputStream bis = new BufferedInputStream(fis);
                     ZipInputStream stream = new ZipInputStream(bis)) {

                    ZipEntry entry;
                    while ((entry = stream.getNextEntry()) != null) {

                        Path filePath = outDir.resolve(entry.getName());

                        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
                             BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)) {

                            int len;
                            while ((len = stream.read(buffer)) > 0) {
                                bos.write(buffer, 0, len);
                            }
                            context.addSummeryLine("Step result is Success!");
                            result = StepResult.SUCCESS;
                            context.storeDataValue("RESULT", "Success");
                        }
                    }
                } catch (IOException e) {
                    result = StepResult.FAILURE;
                    context.storeDataValue("RESULT", "Failure. Error while trying to perform operation");
                    context.addSummeryLine("Step is failure. Error while trying to perform operation.");
                    context.addLogLine(new LogLine("Error while trying to perform operation.", LocalTime.now()));
                    throw new RuntimeException(e);
                }
            } catch (InvalidPathException ex) {
                result = StepResult.FAILURE;
                context.storeDataValue("RESULT", "Failure. Error while trying to perform operation");
                context.addSummeryLine("Step is failure. Error while trying to perform operation.");
                context.addLogLine(new LogLine("Error while trying to perform operation.", LocalTime.now()));
            }
        }

        context.storeResult(result);
        Instant end = Instant.now();
        LocalTime endTime = LocalTime.now();
        context.storeEndTime(endTime);
        Duration duration = Duration.between(start, end);
        context.storeDuration(duration);
        return result;
    }

    private void zipFile(String filePath, ZipOutputStream zos) throws IOException {
        File file = new File(filePath);

        try (FileInputStream fis = new FileInputStream(file)) {
            // Create a new entry for the file
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);

            // Read the file and write its contents to the zip output stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }

            // Close the entry
            zos.closeEntry();
        }
    }

    private void zipDirectory(String directoryPath, ZipOutputStream zos) throws IOException {
        File directory = new File(directoryPath);

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively zip subdirectories
                    zipDirectory(file.getAbsolutePath(), zos);
                } else {
                    // Zip individual files
                    zipFile(file.getAbsolutePath(), zos);
                }
            }
        }
    }
}


