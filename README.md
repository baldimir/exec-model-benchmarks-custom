# Executable model benchmarks #

This project serves for custom benchmarking of executable model. The benchmarks included compare performance of executable model with DRL performance. By default, the benchmarks generate 5000 rules. One benchmark measures creating a KJar out of these rules, the second one measures loading of such generated KJar. 

Benchmark classes are named
`org.drools.benchmarks.buildtime.BuildKJarFromResourceBenchmark`
`org.drools.benchmarks.buildtime.BuildKieBaseFromContainerBenchmark`

## How to use ## 

1. Build the project with Maven:  

`mvn clean install`  

2. Run the benchmarks with:

`java -jar exec-model-benchmarks-custom.jar`

You can also run a single benchmark with

`java -jar exec-model-benchmarks-custom.jar org.drools.benchmarks.buildtime.BuildKJarFromResourceBenchmark`

or with  

`java -jar exec-model-benchmarks-custom.jar org.drools.benchmarks.buildtime.BuildKieBaseFromContainerBenchmark`

## How to use your rules in the benchmarks ##

If you want to use your own rules, you need to 

1. If your rules contain your own custom domain model, you need to add the jars with the model to the dependencies list in the pom.xml file of the project. 
2. If it is enough to use just one DRL file, there is one empty file prepared in the resources folder, to which you can copy your rules. You can also replace this file with your own file. It is also possible to use more DRL files at once.  
2.1. If one DRL file is enough for you, uncomment the appropriate drlResource initialization in the benchmarks and comment out the initialization, where the generated rules are used.  
2.2. If you need to use more DRL files, you need to replace the prepared drlResource initialization with your own initialization, that will create resources representing all DRL files that you want to use. In this case you need to add the created resources into the benchmark method as parameters. Please see the comment in the benchmark method to see how to do this. 
3. Rebuild the benchmarks with  
`mvn clean install`
4. Run the benchmarks with  
`java -jar exec-model-benchmarks-custom.jar`