package com.github.bea4dev.neon;

import org.contan_lang.ContanEngine;
import org.contan_lang.ContanModule;
import org.contan_lang.variables.primitive.JavaClassObject;
import thpmc.vanilla_source.api.VanillaSourceAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Script {

    public static final String SCRIPT_PATH_NAME = "plugins/NeonGenerator/scripts";
    public static final Path SCRIPT_PATH = Paths.get(SCRIPT_PATH_NAME);

    public static void loadAllModules() throws Exception {
        Neon.getPlugin().getLogger().info("Load all Contan modules.");

        File file = new File(SCRIPT_PATH_NAME);
        file.mkdirs();

        ContanEngine contanEngine = VanillaSourceAPI.getInstance().getContanEngine();
        contanEngine.setRuntimeVariable("NeonAPI", new JavaClassObject(contanEngine, NeonAPI.class));

        //Load all files
        List<Path> scriptFilePaths;
        try (Stream<Path> paths = Files.walk(SCRIPT_PATH)) {
            scriptFilePaths = paths.filter(Files::isRegularFile).collect(Collectors.toList());
        }

        List<ContanModule> modules = new ArrayList<>();

        //Compile all source codes
        for (Path path : scriptFilePaths) {
            if (!path.toString().endsWith(".cntn")) {
                continue;
            }

            StringBuilder script = new StringBuilder();

            try{
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()) , StandardCharsets.UTF_8));
                String data;
                while ((data = bufferedReader.readLine()) != null) {
                    script.append(data);
                    script.append('\n');
                }
                bufferedReader.close();
            }catch(Exception e){
                throw new IllegalStateException("Failed to load script file '" + path.toFile().getName() + "'.", e);
            }

            String modulePathName = file.toURI().relativize(path.toFile().toURI()).toString();

            Neon.getPlugin().getLogger().info("Compile script : " + modulePathName);
            ContanModule contanModule = contanEngine.compile(modulePathName, script.toString());

            modules.add(contanModule);
        }

        for (ContanModule contanModule : modules) {
            Neon.getPlugin().getLogger().info("Initialize module : " + contanModule.getRootName());
            contanModule.initialize(contanEngine.getMainThread());
        }
    }

}
