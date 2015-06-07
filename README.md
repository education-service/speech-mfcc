
## 基于MFCC的语音特征提取和识别

> 基于JAVA实现。

## 使用说明

> 注意：运行Jar文件时，运行命令要和data在同一个目录下面。

### 执行Maven命令

`打包`：mvn clean package

`查看`：查看target目录下是否有speech-mfcc-0.0.1-SNAPSHOT-jar-with-dependencies.jar文件

`将该jar包和data放到一个目录下面`：如：创建文件夹release,jar包和data分别位于：release/speech-mfcc-0.0.1-SNAPSHOT-jar-with-dependencies.jar和release/data，测试的时候注意将data下面的ann,knn,svm,svm-iris文件夹清空。

### KNN分类

`处理原始语音数据`：java -jar speech-mfcc-0.0.1-SNAPSHOT-jar-with-dependencies.jar MFCCFeatureMain knn

`执行分类算法`：java -jar speech-mfcc-0.0.1-SNAPSHOT-jar-with-dependencies.jar KNNMain 40

> 注意需要传入一个参数K，这里取40，可以随意设定。

### LibSVM分类

`处理原始语音数据`：java -jar speech-mfcc-0.0.1-SNAPSHOT-jar-with-dependencies.jar MFCCFeatureMain svmiris

`执行分类算法`：java -jar speech-mfcc-0.0.1-SNAPSHOT-jar-with-dependencies.jar SVMIrisMain data/svm-iris/train.data data/svm-iris/output

> 注意这里需要传入两个参数，一个是数据集，另一个是输出结果目录，这里取data/svm-iris/train.data和data/svm-iris/output。

### SimpleSVM分类

`处理原始语音数据`：java -jar speech-mfcc-0.0.1-SNAPSHOT-jar-with-dependencies.jar MFCCFeatureMain svm

`执行分类算法`：java -jar speech-mfcc-0.0.1-SNAPSHOT-jar-with-dependencies.jar SimpleSVM 7000

> 注意这里需要传入一个参数maxIters，这里取7000，可以随意设定。

### ANN分类

`处理原始语音数据`：java -jar speech-mfcc-0.0.1-SNAPSHOT-jar-with-dependencies.jar MFCCFeatureMain ann

`执行分类算法`：java -jar speech-mfcc-0.0.1-SNAPSHOT-jar-with-dependencies.jar SimpleNeuralNetwork 50000

> 注意这里需要传入一个参数maxIters，这里取50000，可以随意设定。