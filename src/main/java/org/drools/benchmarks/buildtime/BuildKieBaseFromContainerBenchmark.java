/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.benchmarks.buildtime;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import org.drools.benchmarks.common.DRLProvider;
import org.drools.benchmarks.common.providers.RulesWithJoinsProvider;
import org.drools.benchmarks.common.util.BuildtimeUtil;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.util.FileUtils;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 15, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(3)
public class BuildKieBaseFromContainerBenchmark {

    @Param({"true", "false"})
    private boolean useCanonicalModel;

    @Param({"5000"})
    private int numberOfRules;

    private ReleaseId releaseId;
    private KieServices kieServices;
    private KieBaseConfiguration kieBaseConfiguration;

    @Setup
    public void createKJar() throws IOException {
        kieServices = KieServices.get();
        kieBaseConfiguration = kieServices.newKieBaseConfiguration();
        
        // Use generated rules. 
        final DRLProvider drlProvider = new RulesWithJoinsProvider(4, false, true);
        final Resource drlResource = KieServices.get().getResources()
                                                .newReaderResource(new StringReader(drlProvider.getDrl(numberOfRules)))
                                                .setResourceType(ResourceType.DRL)
                                                .setSourcePath("drlFile.drl");

        // You can write the kjar to your custom location by uncommenting this line and specifying your custom kjar path.
//        final File kjarFile = new File("add-your-custom-path-here.jar");
        final File kjarFile = FileUtils.tempFile("jar");

        // Uncomment to use testDrl.drl file. (and comment out the generated rules)
        // drlResource = KieServices.get().getResources()
        //                          .newClassPathResource("testDrl.drl")
        //                          .setResourceType(ResourceType.DRL)
        //                          .setSourcePath("drlFile.drl");

        // You can add more resources as parameters of this method call. E.g. it could be used as 
        // return BuildtimeUtil.createKJarFromResources(useCanonicalModel, kjarFile, drlResource1, drlResource2, ...);
        releaseId = BuildtimeUtil.createKJarFromResources(useCanonicalModel, kjarFile, drlResource);
    }

    @Benchmark
    public KieBase getKieBaseFromContainer() {
        return kieServices.newKieContainer(releaseId).newKieBase(kieBaseConfiguration);
    }

    /**
     * Just for debugging purposes. For proper measurement, run the benchmarks jar from command line.
     * To debug in IDE, simply run this main() method in a debug mode. All breakpoints in the code should work.
     *
     * @param args
     * @throws RunnerException
     */
    public static void main(final String[] args) throws RunnerException {
        final Options opt = new OptionsBuilder()
                .include(BuildKieBaseFromContainerBenchmark.class.getSimpleName())
                .forks(0)
                .build();

        new Runner(opt).run();
    }
}
