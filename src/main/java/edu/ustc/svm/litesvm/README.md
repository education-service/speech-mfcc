litesvm
=======

A super light-weight and easy to use Java SVM library. 

```java
final BinaryClassifier classifier = LiteSVM.trainBinaryClassifier(Lists.newArrayList(
  new BinarySample(false, 0),
  new BinarySample(true, 1)
));

classifier.predict(0.2) // False
classifier.predict(0.3) // False
classifier.predict(0.4) // False
classifier.predict(0.5) // ???
classifier.predict(0.6) // True

final BinaryClassifier classifier2D = LiteSVM.trainBinaryClassifier(Lists.newArrayList(
  new BinarySample(false, 1, 0),
  new BinarySample(false, 1, 1),
  new BinarySample(true, 0, 0),
  new BinarySample(true, 0, 1)
));

classifier2D.predict(0.2, 0.123)   // True
classifier2D.predict(0.3, 0.812)   // True
classifier2D.predict(0.4, 0.231)   // True
classifier2D.predict(0.5, 0.451)   // ???
classifier2D.predict(0.6, 0.711)   // False
classifier2D.predict(0.7, 0.143)   // False
```

##Installation

LiteSVM can be install directly from Github using Maven by simply adding the following `repository` and `dependency` into your `pom.xml`.

```xml
<repositories>
  ...
  <repository>
    <id>litesvm-mvn-repo</id>
      <url>https://raw.github.com/wanasit/LiteSVM/mvn-repo/</url>
      <snapshots>
           <enabled>true</enabled>
           <updatePolicy>always</updatePolicy>
      </snapshots>
    </repository>
</repositories>
```

```xml
<dependencies>
  ...
  <dependency>
       <groupId>com.wanasit</groupId>
       <artifactId>litesvm</artifactId>
       <version>LATEST</version>
  </dependency>
</dependencies>
```
