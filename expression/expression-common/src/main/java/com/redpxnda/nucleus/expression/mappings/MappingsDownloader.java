package com.redpxnda.nucleus.expression.mappings;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MappingsDownloader {
    protected static final String YARN_URL = "https://maven.fabricmc.net/net/fabricmc/yarn/%1$s/yarn-%1$s.jar";
    protected static final String MOJMAP_URL = "https://piston-data.mojang.com/v1/objects/%1$s/client.txt";
    protected static final String SRG_URL = "https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_config/%1$s/mcp_config-%1$s.zip";

    public static String getYarnUrl(String version) {
        return YARN_URL.formatted(version);
    }

    public static String getMojmapUrl(String version) {
        return MOJMAP_URL.formatted(version);
    }

    public static String getSrgUrl(String version) {
        return SRG_URL.formatted(version);
    }

    public static void downloadYarnMappings(String version) {
        try {
            String currentDirectory = System.getProperty("user.dir");
            File file = new File(currentDirectory + "/expression/expression-common/src/main/resources/data/nucleus/mappings");
            file.mkdirs();
            Path path = Files.write(file.toPath().resolve("yarn.jar"), new URL(getYarnUrl(version)).openStream().readAllBytes());

            JarFile jar = new JarFile(path.toFile());
            JarEntry entry = jar.getJarEntry("mappings/mappings.tiny");

            InputStream input = jar.getInputStream(entry);
            Files.copy(input, file.toPath().resolve("yarn.tiny"));

            jar.close();
            Files.delete(path);
        } catch (Exception e) {
            System.out.println("Error downloading yarn mappings file: ");
            e.printStackTrace();
        }
    }

    public static void downloadMojangMappings(String version) {
        try {
            String currentDirectory = System.getProperty("user.dir");
            File file = new File(currentDirectory + "/expression/expression-common/src/main/resources/data/nucleus/mappings");
            file.mkdirs();
            Files.write(file.toPath().resolve("mojmap.txt"), new URL(getMojmapUrl(version)).openStream().readAllBytes());
        } catch (Exception e) {
            System.out.println("Error downloading mojmap mappings file: ");
            e.printStackTrace();
        }
    }

    /*public static void downloadIntermediary(String version) {
        try {
            String currentDirectory = System.getProperty("user.dir");
            File file = new File(currentDirectory + "/expression/expression-common/src/main/resources/data/nucleus/mappings");
            file.mkdirs();
            Files.write(file.toPath().resolve("intermediary.tiny"), new URL("https://raw.githubusercontent.com/FabricMC/intermediary/master/mappings/" + version).openStream().readAllBytes());
        } catch (Exception e) {
            System.out.println("Error downloading intermediary mappings file: ");
            e.printStackTrace();
        }
    }*/

    public static void downloadSrgMappings(String version) {
        try {
            String currentDirectory = System.getProperty("user.dir");
            File file = new File(currentDirectory + "/expression/expression-common/src/main/resources/data/nucleus/mappings");
            file.mkdirs();
            Path path = Files.write(file.toPath().resolve("srg.zip"), new URL(getSrgUrl(version)).openStream().readAllBytes());

            ZipFile zip = new ZipFile(path.toFile());
            ZipEntry entry = zip.getEntry("config/joined.tsrg");

            InputStream input = zip.getInputStream(entry);
            Files.copy(input, file.toPath().resolve("srg.tsrg"));

            zip.close();
            Files.delete(path);
        } catch (Exception e) {
            System.out.println("Error downloading srg mappings file: ");
            e.printStackTrace();
        }
    }
}
