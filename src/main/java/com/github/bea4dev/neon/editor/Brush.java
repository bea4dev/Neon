package com.github.bea4dev.neon.editor;

import com.github.bea4dev.neon.NeonAPI;
import com.github.bea4dev.neon.generator.Generator;
import com.github.bea4dev.neon.generator.ScriptFunctionHolder;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.contan_lang.ContanEngine;
import org.contan_lang.ContanModule;
import org.contan_lang.syntax.exception.ContanParseException;
import thpmc.vanilla_source.api.VanillaSourceAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Brush {

    private static final Map<String, Brush> brushMap = new HashMap<>();

    private static ContanModule contanModule;

    public static void initialize() throws ContanParseException {
        ContanEngine contanEngine = VanillaSourceAPI.getInstance().getContanEngine();
        contanModule = contanEngine.compile(".neon_internal", "" +
                "function run(thread, generator, list) {\n" +
                "    sync (generator.thread) {\n" +
                "        all holder in list {\n" +
                "            const expression = holder.expression\n" +
                "            expression(generator)\n" +
                "        }\n" +
                "    }.await()\n" +
                "}");

        load();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void load() {
        File dir = new File("plugins/VanillaSource/camera");

        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if (files != null) {
            if (files.length == 0) {
                files = dir.listFiles();
            }
        }

        if (files != null) {
            for (File file : files) {
                if (!file.getName().contains(".yml")) {
                    continue;
                }

                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

                String id = file.getName().replace(".yml", "");
                String displayName = yml.getString("display-name");
                List<String> functionIdList = yml.getStringList("function-id-list");
                brushMap.put(id, new Brush(id, displayName, functionIdList));
            }
        }
    }




    private final String id;
    private final String displayName;
    private final List<String> functionIdList;

    public Brush(String id, String displayName, List<String> functionIdList) {
        this.id = id;
        this.displayName = displayName;
        this.functionIdList = functionIdList;
    }

    public void run(Block block) throws ExecutionException, InterruptedException {
        Generator generator = NeonAPI.getGenerator(block);
        List<ScriptFunctionHolder> holderList = new ArrayList<>();
        for (String functionId : functionIdList) {
            holderList.add(NeonAPI.functionMap.get(functionId));
        }
        contanModule.invokeFunction(generator.thread, "run", generator.thread, generator, holderList);
    }

}
