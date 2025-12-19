# doris-gen - 优雅的Doris数据库代码生成工具

## 项目介绍

`doris-gen`是一个基于MyBatis-Plus的Doris数据库代码生成工具，能够快速生成数据库表对应的Java实体类、Mapper、Service等代码，提高开发效率。

## 功能特点

- 基于MyBatis-Plus生成器，支持自定义模板
- 支持命令行参数配置，灵活方便
- 可打包为独立可运行JAR，便于在多个项目间共享使用
- 支持配置文件方式管理数据库连接和生成规则
- 生成代码结构清晰，符合最佳实践

## 快速开始

### 环境要求

- Java 21+
- Maven 3.6+

### 下载与安装

1. 从GitHub下载最新版本的JAR包

2. 或者自行编译：

```bash
git clone <repository-url>
cd doris-generator
mvn clean package -DskipTests
```

编译完成后，可执行JAR将位于`target`目录下，文件名为`doris-gen-1.0.0-doris-gen.jar`。

### 使用方法

#### 1. 使用命令行参数

```bash
java -cp doris-gen-1.0.0-doris-gen.jar com.github.palagend.generator.DorisCodeGenerator -h
```

查看所有可用参数：

```
usage: java -cp doris-gen-1.0.0-doris-gen.jar com.github.palagend.generator.DorisCodeGenerator
 -h,--help              显示帮助信息
 -o,--outputDir <arg>   指定代码生成输出目录
 -p,--properties <arg>  指定配置文件路径
 -t,--tables <arg>      指定要生成代码的表名，多个表用逗号分隔
```

#### 2. 使用配置文件

1. 从JAR包中提取配置文件模板：

```bash
mkdir -p config
jar xf doris-gen-1.0.0-doris-gen.jar config/doris-generator.properties
```

2. 修改配置文件`config/doris-generator.properties`：

```properties
# 数据库连接信息
jdbcUrl=jdbc:mysql://localhost:9030/database_name?useUnicode=true&characterEncoding=utf-8
userName=root
passWord=root

# 要生成的表名，多个表用逗号分隔
includeTables=table1,table2,table3

# 输出目录
outputDir=./generated-code
```

3. 运行生成器：

```bash
java -cp doris-gen-1.0.0-doris-gen.jar com.github.palagend.generator.DorisCodeGenerator -p config/doris-generator.properties
```

## 配置详解

### 核心配置项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| jdbcUrl | 数据库连接URL | - |
| userName | 数据库用户名 | - |
| passWord | 数据库密码 | - |
| includeTables | 要生成的表名列表 | - |
| outputDir | 代码输出目录 | ./ |
| packagePath | 生成代码的包路径 | com.github.palagend |
| author | 作者名 | generator |
| tablePrefix | 表前缀，生成类名时会去掉前缀 | - |

### 高级配置

在代码生成器中还支持更多高级配置，如模板自定义、生成策略等，可通过修改源代码中的配置进行调整。

## 示例

### 使用命令行参数指定表名和输出目录

```bash
java -cp doris-gen-1.0.0-doris-gen.jar com.github.palagend.generator.DorisCodeGenerator \
    -o ./src/main/java \
    -t user,order,product
```

### 使用配置文件生成所有表

```bash
java -cp doris-gen-1.0.0-doris-gen.jar com.github.palagend.generator.DorisCodeGenerator \
    -p config/doris-generator.properties
```

## 生成的代码结构

```
outputDir
└── com
    └── github
        └── palagend
            ├── entity
            │   └── TableEntity.java
            ├── mapper
            │   └── TableMapper.java
            ├── service
            │   ├── TableService.java
            │   └── impl
            │       └── TableServiceImpl.java
            └── controller
                └── TableController.java
```

## 注意事项

1. 确保数据库连接信息正确，能够正常访问Doris数据库
2. 指定的表名必须存在于数据库中
3. 输出目录需要有写入权限
4. 首次使用建议先备份相关目录，避免覆盖现有代码

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。

## 许可证

本项目采用MIT许可证。

