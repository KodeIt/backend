package com.kodeit.backend.util;

import com.kodeit.backend.entity.Code;
import com.kodeit.backend.enums.ExecutionStatus;
import com.kodeit.backend.exception.code.InternalError;
import com.kodeit.backend.modal.ExecutionOutput;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

public class CodeRunner {

    private static List<String> createFilesAndFolders(Code code) throws InternalError {
        try {
            String folderName = UUID.randomUUID().toString().substring(0, 6);
            String fileName = UUID.randomUUID().toString().substring(0, 6);

            switch (code.getLanguage()) {
                case C -> fileName = fileName.concat(".c");
                case CPP -> fileName = fileName.concat(".cpp");
                case JAVA -> fileName = "Code.java"; // Because java won't execute without a public class
                case SHELL -> fileName = fileName.concat(".sh");
                case PYTHON -> fileName = fileName.concat(".py");
                case TYPESCRIPT -> fileName = fileName.concat(".ts");
                case JAVASCRIPT -> fileName = fileName.concat(".js");
                default -> throw new InternalError();
            }

            File folder = new File(folderName);
            File codeFile = new File("codes/" + folderName + "/" + fileName);
//            if (!codeFile.createNewFile()) throw new InternalError();
            codeFile.getParentFile().mkdirs();
            codeFile.createNewFile();
            File input = new File("codes/" + folderName + "/input.txt");
//            if(!input.createNewFile()) throw new InternalError();
            input.createNewFile();

            FileWriter fileWriter = new FileWriter(codeFile);
            fileWriter.write(code.getCode());
            fileWriter.flush();
            fileWriter.close();

            fileWriter = new FileWriter(input);
            fileWriter.write(code.getInput());
            fileWriter.flush();
            fileWriter.close();
            return List.of(fileName, folderName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalError();
        }
    }

    public static ExecutionOutput execute(Code code) throws InternalError{
        List<String> metadata = createFilesAndFolders(code);
        String fileName = metadata.get(0);
        String folderName = metadata.get(1);
        String executionCommand = "";

        switch (code.getLanguage()) {
            case C -> executionCommand = "gcc -o {0}/output {0}/{1} && timeout 1s ./{0}/output < {0}/input.txt; rm -rf {0}";
            case CPP -> executionCommand = "g++ -o {0}/output {0}/{1} && timeout 1s ./{0}/output < {0}/input.txt; rm -rf {0}";
            case PYTHON -> executionCommand = "timeout 1s python3 {0}/{1} < {0}/input.txt; rm -rf {0}";
            case SHELL -> executionCommand = "timeout 1s sh {0}/{1} < {0}/input.txt; rm -rf {0}";
            case JAVA -> executionCommand = "javac {0}/{1} && timeout 1s java {0}/{1} < {0}/input.txt; rm -rf {0}";
            case TYPESCRIPT -> executionCommand = "timeout 1s npx ts-node {0}/{1} < {0}/input.txt; rm -rf {0}";
            case JAVASCRIPT -> executionCommand = "timeout 1s npx node {0}/{1} < {0}/input.txt; rm -rf {0}";
            default -> throw new InternalError();
        }

        try {
            boolean failed = false;
            Runtime rt = Runtime.getRuntime();
            String[] commands = {
                    "/bin/bash",
                    "-c",
                    MessageFormat.format(executionCommand, folderName, fileName)
            };
            Process proc = null;
            proc = rt.exec(commands);

            assert proc != null;
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            String s = null;
            StringJoiner output = new StringJoiner(System.lineSeparator());
            while ((s = stdError.readLine()) != null) {
                output.add(s);
                failed = true;
            }
            while ((s = stdInput.readLine()) != null) {
                output.add(s);
            }
            FileUtils.deleteDirectory(new File(folderName));
            if (failed)
                return new ExecutionOutput(ExecutionStatus.FAILED, output.toString());
            else
                return new ExecutionOutput(ExecutionStatus.SUCCESS, output.toString());
        } catch (IOException e) {
            throw new InternalError();
        }
    }
}