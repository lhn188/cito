# CITO — Class Integration Test Order

CITO 是一个研究型工具，用于为 Java 项目计算最优的**类集成测试顺序**（Class Integration Test Order）。它通过静态分析提取类之间的依赖关系图，然后使用图算法确定能够最小化测试桩（stub/mock）复杂度的测试顺序。

## 技术栈

- **Java 8**
- **Spring Boot 2.2.6** — Web 框架
- **Maven** — 构建工具
- **Soot 3.3.0** — Java 字节码静态分析框架
- **Thymeleaf** — 模板引擎
- **Apache POI 3.17** — Excel 解析
- **dom4j 1.6.1** — XML 解析
- **fastjson 1.2.56** — JSON 序列化
- **Jython 2.7.0** — Java 中的 Python 解释器

## 快速开始

### 环境要求

- JDK 8
- Maven 3.x

### 构建与运行

```bash
# 编译
./mvnw compile

# 打包
./mvnw package

# 运行测试
./mvnw test

# 启动 Web 应用（默认端口 8080）
./mvnw spring-boot:run

# 直接运行命令行分析（不经过 Web UI）
./mvnw exec:java -Dexec.mainClass="com.zzc.CITO.manager.Manager"
```

启动后访问 `http://localhost:8080/enter` 进入上传页面。

## 核心功能

### 1. 静态依赖分析

两种分析模式，根据项目名称自动切换：

| 模式 | 适用项目 | 说明 |
|------|---------|------|
| **Soot 分析** | 默认模式 | 对 Java 字节码进行静态分析，自动提取类间的属性依赖、方法依赖、继承关系、接口实现 |
| **CSV 解析** | ANT, ATM, DNS, SPM | 从预处理的 Excel 和 XML 文件中读取已解析的依赖数据 |

Soot 分析会自动过滤掉 JDK 和常见第三方库的类，只保留应用自身的类进行分析。

### 2. 类重要性评估

采用改进的 HITS 算法（ClassHITS）评估每个类在网络中的重要性，综合考虑：
- **入度/出度**（Authority/Hub）
- **K-核分解**（K-core decomposition）
- **结构洞**（Structural holes）
- **熵权法**（Entropy weight method）平衡特征数量（NOF）与耦合度

### 3. 集成测试顺序生成

1. **破环**：使用 Kosaraju 算法识别强连通分量（SCC），枚举所有环路，按边权重→耦合度→重要性优先级移除边，将依赖图转为有向无环图（DAG）
2. **拓扑排序**：按类重要性降序排列，生成测试顺序

## 依赖关系类型

系统识别 5 种依赖关系类型：

| 类型 | 常量 | 说明 |
|------|------|------|
| 无 | NONE (0) | 无依赖 |
| 关联 | IAS (1) | 属性引用、方法参数/返回值、方法调用 |
| 聚合 | IAG (2) | 多个属性引用同一类，升级为聚合 |
| 继承 | II (3) | 继承或接口实现 |
| 动态依赖 | Dy (4) | 基于继承的传递性依赖 |

## 项目结构

```
cito-master/
├── src/main/java/com/zzc/
│   ├── CitoProjectApplication.java          # Spring Boot 入口
│   └── CITO/
│       ├── analyzer/                         # 静态分析模块
│       │   ├── Analyzer.java                 # Soot 字节码分析（默认模式）
│       │   ├── Parser.java                   # CSV/XML 数据解析模式
│       │   └── SootOption.java               # Soot 配置
│       ├── Base/                             # 核心数据模型
│       │   ├── TClass.java                   # 图节点：类
│       │   ├── TEdge.java                    # 图边：依赖关系
│       │   ├── rType.java                    # 关系类型常量
│       │   ├── C.java                        # 前端展示 DTO：类
│       │   └── E.java                        # 前端展示 DTO：边
│       ├── BCN/                              # 图算法与测试顺序生成
│       │   ├── BCN.java                      # 主控器
│       │   ├── ORD.java                      # 破环算法
│       │   ├── Kosaraju.java                 # Kosaraju SCC + 环路枚举
│       │   ├── TestOrderBuilder.java         # 拓扑排序生成测试顺序
│       │   ├── ClassHITS.java                # 改进的 HITS 重要性评估
│       │   ├── newCLassHITS.java             # 新版 HITS 算法
│       │   └── getShang.java                 # 熵值计算辅助
│       ├── Qlearning/                        # 强化学习模块（实验性）
│       │   ├── Qlearning.java
│       │   ├── NewEnvironment.java
│       │   ├── Environment.java
│       │   ├── CITOAgent.java
│       │   └── OldQlearning.java
│       ├── SCplx/                            # 耦合度计算
│       │   ├── SCplx.java                    # 结构耦合度
│       │   └── NewSCplx.java                 # 熵权法权重
│       ├── manager/                          # 管理层
│       │   ├── Manager.java                  # 核心调度器（main 入口）
│       │   ├── InitInformation.java          # 数据初始化
│       │   ├── SystemInfo.java               # 系统信息 DTO
│       │   └── ResultData.java               # 结果数据 DTO
│       ├── controller/
│       │   └── homeController.java           # Web 控制器
│       ├── util/                             # 工具类
│       │   ├── unZip.java                    # ZIP 解压
│       │   ├── XMLReader.java                # XML 依赖图读取
│       │   ├── ExcelUtil.java                # Excel 读取
│       │   └── exportCSV.java                # CSV 导出
│       └── test/                             # 测试样本类（A-H）
├── src/main/resources/
│   ├── templates/
│   │   ├── display.html                      # 文件上传页面
│   │   └── staticAnalysis.html               # 分析结果展示页
│   └── static/                               # 静态资源（Bootstrap 等）
├── input/                                    # 预分析数据集
├── input_analysis/                           # 上传文件解压目录
├── input_analysis_1b/                        # Soot 分析输入目录
├── input_analysis_2/、input_analysis_2b/     # 其他分析阶段数据
└── CSV/                                      # CSV 输出目录
```

## Web 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/enter` | GET | 文件上传页面 |
| `/fileUpload` | POST | 上传 ZIP 文件并自动解压 |
| `/staticPic?Name=<项目名>` | GET | 运行分析并展示结果（类图、测试顺序） |

## 注意事项

### 硬编码路径

源码中有多处硬编码的绝对路径，部署前需要修改：

| 位置 | 当前路径 | 用途 |
|------|---------|------|
| `homeController.java:42` | `F:\CITO\CITOProject\input_analysis` | 上传文件目标目录 |
| `Parser.java:22` | `D:\cito_important\cito_modified\input\` | CSV 解析模式输入 |
| `SootOption.java:15` | `<user.dir>\cito_modified\input_analysis_1b\<name>` | Soot 分析输入 |
| `Manager.java:117` | `D:\CITO\CITOProject\input\` | CSV 导出输出 |
| `unZip.java:18-19` | `F:\CITO\CITOProject\input_analysis` | ZIP 解压源/目标 |

### 运行 Manager.main() 准备工作

如果直接运行 `Manager.main()`（绕过 Web UI），需要：
1. 将被分析的 Java 项目的 `.class` 文件放到 `input_analysis_1b/<项目名>/` 目录下
2. 修改 `Manager.main()` 中的项目名参数（当前为 "Ant"）

### 测试样本

`src/main/java/com/zzc/test/` 下的 A-H 类构成了特定的依赖关系网络（包含继承、聚合、关联），可用于验证分析器正确性。
