package com.github.palagend.generator;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import lombok.Getter;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DorisCodeGenerator {

    private static GeneratorConfig config = new GeneratorConfig();

    public static void main(String[] args) {
        // 创建命令行选项
        Options options = createOptions();

        // 创建命令行解析器
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            // 解析命令行参数
            CommandLine cmd = parser.parse(options, args);

            // 处理帮助信息
            if (cmd.hasOption("help")) {
                printHelp(formatter, options);
                return;
            }

            // 处理版本信息
            if (cmd.hasOption("version")) {
                printVersion();
                return;
            }

            // 1. 加载配置文件（如果指定了配置文件路径）
            if (cmd.hasOption("config")) {
                loadConfigFromFile(cmd.getOptionValue("config"));
            } else {
                loadConfigFromFile();
            }

            // 2. 解析命令行参数（优先级高于配置文件）
            parseCommandLineArgs(cmd);

            // 3. 生成代码
            generateCode();

        } catch (ParseException e) {
            System.err.println("参数解析错误: " + e.getMessage());
            formatter.printHelp("DorisCodeGenerator", options);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("程序执行错误: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * 创建命令行选项定义
     */
    private static Options createOptions() {
        Options options = new Options();

        // 帮助信息
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("显示帮助信息")
                .build());

        // 版本信息
        options.addOption(Option.builder("v")
                .longOpt("version")
                .desc("显示版本信息")
                .build());

        // 配置文件
        options.addOption(Option.builder("c")
                .longOpt("config")
                .hasArg()
                .argName("FILE")
                .desc("指定配置文件路径")
                .build());

        // 数据库配置
        options.addOption(Option.builder("u")
                .longOpt("url")
                .hasArg()
                .argName("URL")
                .desc("数据库JDBC URL")
                .build());

        options.addOption(Option.builder("n")
                .longOpt("username")
                .hasArg()
                .argName("NAME")
                .desc("数据库用户名")
                .build());

        options.addOption(Option.builder("p")
                .longOpt("password")
                .hasArg()
                .argName("PASSWORD")
                .desc("数据库密码")
                .build());

        // 生成配置
        options.addOption(Option.builder("a")
                .longOpt("author")
                .hasArg()
                .argName("AUTHOR")
                .desc("代码作者")
                .build());

        options.addOption(Option.builder("o")
                .longOpt("output")
                .hasArg()
                .argName("DIR")
                .desc("代码输出目录")
                .build());

        options.addOption(Option.builder("P")
                .longOpt("package")
                .hasArg()
                .argName("PACKAGE")
                .desc("父包名")
                .build());

        options.addOption(Option.builder("t")
                .longOpt("tables")
                .hasArg()
                .argName("TABLES")
                .desc("包含的表名（逗号分隔）")
                .build());

        options.addOption(Option.builder("pf")
                .longOpt("prefix")
                .hasArg()
                .argName("PREFIX")
                .desc("表前缀")
                .build());

        // 模块控制
        options.addOption(Option.builder("s")
                .longOpt("service")
                .hasArg()
                .argName("BOOL")
                .desc("是否生成Service（true/false）")
                .build());

        options.addOption(Option.builder("si")
                .longOpt("service-impl")
                .hasArg()
                .argName("BOOL")
                .desc("是否生成ServiceImpl（true/false）")
                .build());

        options.addOption(Option.builder("ct")
                .longOpt("controller")
                .hasArg()
                .argName("BOOL")
                .desc("是否生成Controller（true/false）")
                .build());

        return options;
    }

    /**
     * 显示帮助信息
     */
    private static void printHelp(HelpFormatter formatter, Options options) {
        System.out.println("Doris代码生成器 - 基于MyBatis Plus的代码自动生成工具");
        System.out.println();
        formatter.printHelp("java -cp your-app.jar com.github.palagend.generator.DorisCodeGenerator", options);
        System.out.println();
        System.out.println("使用示例:");
        System.out.println("  基本用法: java -cp your-app.jar com.github.palagend.generator.DorisCodeGenerator");
        System.out.println("  指定配置文件: java -cp your-app.jar com.github.palagend.generator.DorisCodeGenerator -c config.properties");
        System.out.println("  自定义数据库: java -cp your-app.jar com.github.palagend.generator.DorisCodeGenerator -u jdbc:mysql://localhost:9030/test -n root -p 123456");
        System.out.println("  指定表生成: java -cp your-app.jar com.github.palagend.generator.DorisCodeGenerator -t \"table1,table2,table3\"");
        System.out.println("  启用Service: java -cp your-app.jar com.github.palagend.generator.DorisCodeGenerator -s true -si true");
        System.out.println("  显示版本: java -cp your-app.jar com.github.palagend.generator.DorisCodeGenerator -v");
        System.out.println();
        System.out.println("配置文件优先级: 命令行参数 > 指定配置文件 > 默认配置文件 > 内置默认值");
        System.out.println("支持在以下位置放置配置文件: ./doris-generator.properties, ./config/doris-generator.properties, ${user.dir}/config/doris-generator.properties");
    }

    /**
     * 显示版本信息
     */
    private static void printVersion() {
        System.out.println("Doris代码生成器 v1.0.0");
        System.out.println("基于MyBatis Plus 3.5.0 + Apache Commons CLI 1.5.0");
        System.out.println("编译时间: 2025-11-05");
    }

    /**
     * 从默认路径加载配置文件
     */
    private static void loadConfigFromFile() {
        Properties props = new Properties();
        try {
            String[] configPaths = {
                "doris-generator.properties",
                "config/doris-generator.properties",
                System.getProperty("user.dir") + "/config/doris-generator.properties"
            };

            boolean configLoaded = false;
            for (String path : configPaths) {
                try (FileInputStream fis = new FileInputStream(path)) {
                    props.load(fis);
                    config.updateFromProperties(props);
                    configLoaded = true;
                    System.out.println("配置文件加载成功: " + path);
                    break;
                } catch (IOException e) {
                    // 继续尝试下一个路径
                }
            }

            if (!configLoaded) {
                System.out.println("使用默认配置,因为这些路径未找到配置文件："+ JSON.toJSONString(configPaths));
            }

        } catch (Exception e) {
            System.out.println("配置文件加载失败，使用默认配置: " + e.getMessage());
        }
    }

    /**
     * 从指定路径加载配置文件
     */
    private static void loadConfigFromFile(String configFilePath) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            props.load(fis);
            config.updateFromProperties(props);
            System.out.println("配置文件加载成功: " + configFilePath);
        } catch (Exception e) {
            System.out.println("指定配置文件加载失败: " + configFilePath + ", 错误: " + e.getMessage());
            System.out.println("尝试加载默认配置文件...");
            loadConfigFromFile();
        }
    }

    /**
     * 解析命令行参数
     */
    private static void parseCommandLineArgs(CommandLine cmd) {
        Map<String, String> commandArgs = new HashMap<>();

        if (cmd.hasOption("url")) commandArgs.put("jdbcUrl", cmd.getOptionValue("url"));
        if (cmd.hasOption("username")) commandArgs.put("username", cmd.getOptionValue("username"));
        if (cmd.hasOption("password")) commandArgs.put("password", cmd.getOptionValue("password"));
        if (cmd.hasOption("author")) commandArgs.put("author", cmd.getOptionValue("author"));
        if (cmd.hasOption("output")) commandArgs.put("outputDir", cmd.getOptionValue("output"));
        if (cmd.hasOption("package")) commandArgs.put("parentPackage", cmd.getOptionValue("package"));
        if (cmd.hasOption("tables")) commandArgs.put("includeTables", cmd.getOptionValue("tables"));
        if (cmd.hasOption("prefix")) commandArgs.put("tablePrefix", cmd.getOptionValue("prefix"));
        if (cmd.hasOption("service")) commandArgs.put("enableService", cmd.getOptionValue("service"));
        if (cmd.hasOption("service-impl")) commandArgs.put("enableServiceImpl", cmd.getOptionValue("service-impl"));
        if (cmd.hasOption("controller")) commandArgs.put("enableController", cmd.getOptionValue("controller"));

        config.updateFromCommandLine(commandArgs);
    }

    /**
     * 生成代码
     */
    private static void generateCode() {
        System.out.println("开始生成代码，配置信息:");
        System.out.println("JDBC URL: " + config.getJdbcUrl());
        System.out.println("用户名: " + config.getUsername());
        System.out.println("输出目录: " + config.getOutputDir());
        System.out.println("包含表: " + String.join(", ", config.getIncludeTables()));

        try {
            DataSourceConfig.Builder dataSourceBuilder = new DataSourceConfig.Builder(
                    config.getJdbcUrl(),
                    config.getUsername(),
                    config.getPassword()
            ).dbQuery(new DorisQuery());

            FastAutoGenerator.create(dataSourceBuilder)
                .globalConfig(builder -> {
                    builder.author(config.getAuthor())
                           .outputDir(config.getOutputDir())
                           .disableOpenDir()
                           .commentDate("yyyy-MM-dd")
                           .dateType(DateType.ONLY_DATE);
                })
                .packageConfig(builder -> {
                    builder.parent(config.getParentPackage())
                           .entity("entity")
                           .service("service")
                           .serviceImpl("service.impl")
                           .mapper("mapper")
                           .controller("controller");
                })
                .strategyConfig(builder -> {
                    builder.addTablePrefix(config.getTablePrefix())
                           .addInclude(config.getIncludeTables())
                           .entityBuilder()
                               .enableFileOverride()
                               .enableLombok()
                           .mapperBuilder()
                               .enableFileOverride()
                               .superClass(BaseMapper.class)
                               .enableBaseResultMap()
                               .enableBaseColumnList()
                            .serviceBuilder()
                                .enableFileOverride()
                                .disableService()
                                .disableServiceImpl()
                            ;
                })
                // 在MyBatis-Plus 3.5+版本中，使用strategyConfig的entityBuilder、mapperBuilder等来配置生成策略
                // 模板配置已经集成到这些构建器中
                .execute();

            System.out.println("代码生成完成!");

        } catch (Exception e) {
            System.err.println("代码生成失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * 配置类
     */
    @Getter
    static class GeneratorConfig {
        // Getter方法
        private String jdbcUrl = "jdbc:mysql://192.168.108.10:9030/data_studio";
        private String username = "admin";
        private String password = "ctsi@Passw0rd";
        private String author = "MybatisPlusGenerator";
        private String outputDir = System.getProperty("user.dir") + "/src/main/java";
        private String parentPackage = "com.ct.w.datastudio.autogen";
        private String tablePrefix = "app_pubm_sjgf_";
        private boolean enableService = false;
        private boolean enableServiceImpl = false;
        private boolean enableController = false;

        private List<String> includeTables = Arrays.asList(
            "demo","demo2"
        );

        public void updateFromProperties(Properties props) {
            jdbcUrl = props.getProperty("jdbcUrl", jdbcUrl);
            username = props.getProperty("username", username);
            password = props.getProperty("password", password);
            author = props.getProperty("author", author);
            outputDir = props.getProperty("outputDir", outputDir);
            parentPackage = props.getProperty("parentPackage", parentPackage);
            tablePrefix = props.getProperty("tablePrefix", tablePrefix);
            enableService = Boolean.parseBoolean(props.getProperty("enableService", "false"));
            enableServiceImpl = Boolean.parseBoolean(props.getProperty("enableServiceImpl", "false"));
            enableController = Boolean.parseBoolean(props.getProperty("enableController", "false"));

            String tables = props.getProperty("includeTables");
            if (tables != null && !tables.trim().isEmpty()) {
                includeTables = Arrays.asList(tables.split(","));
            }
        }

        public void updateFromCommandLine(Map<String, String> args) {
            if (args.containsKey("jdbcUrl")) jdbcUrl = args.get("jdbcUrl");
            if (args.containsKey("username")) username = args.get("username");
            if (args.containsKey("password")) password = args.get("password");
            if (args.containsKey("author")) author = args.get("author");
            if (args.containsKey("outputDir")) outputDir = args.get("outputDir");
            if (args.containsKey("parentPackage")) parentPackage = args.get("parentPackage");
            if (args.containsKey("tablePrefix")) tablePrefix = args.get("tablePrefix");
            if (args.containsKey("enableService")) enableService = Boolean.parseBoolean(args.get("enableService"));
            if (args.containsKey("enableServiceImpl")) enableServiceImpl = Boolean.parseBoolean(args.get("enableServiceImpl"));
            if (args.containsKey("enableController")) enableController = Boolean.parseBoolean(args.get("enableController"));

            if (args.containsKey("includeTables")) {
                String tables = args.get("includeTables");
                includeTables = Arrays.stream(tables.split(",")).map(String::trim).collect(Collectors.toList());
            }
        }

    }
}