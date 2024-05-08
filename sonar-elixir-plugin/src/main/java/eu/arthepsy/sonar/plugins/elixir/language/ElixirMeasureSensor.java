/*
 * SonarQube Elixir plugin
 * Copyright (C) 2015 Andris Raugulis
 * moo@arthepsy.eu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package eu.arthepsy.sonar.plugins.elixir.language;

//import eu.arthepsy.sonar.plugins.elixir.ElixirConfiguration;
//import org.sonar.api.internal.apachecommons.lang.StringUtils;

//import org.sonar.api.utils.log.Logger;
//import org.sonar.api.utils.log.LoggerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import org.sonar.api.utils.log.*;

import eu.arthepsy.sonar.plugins.elixir.ElixirConfiguration;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.utils.ParsingUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

public class ElixirMeasureSensor implements Sensor {

    private static final String LOG_PREFIX = ElixirConfiguration.LOG_PREFIX;

    private static final Logger LOG = LoggerFactory.getLogger(ElixirMeasureSensor.class);

    private final FileSystem fileSystem;
    private final FilePredicate mainFilePredicate;


    public ElixirMeasureSensor(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.mainFilePredicate = fileSystem.predicates().and(
                fileSystem.predicates().hasType(InputFile.Type.MAIN),
                fileSystem.predicates().hasLanguage(Elixir.KEY));
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Elixir Sensors");
    }

    @Override
    public void execute(SensorContext context) {
        LOG.info("[elixir] analyze");
        for (InputFile file : fileSystem.inputFiles(mainFilePredicate )) {
            processMainFile(file, context);
        }
    }

    private void processMainFile(InputFile inputFile, SensorContext context) {
        List<String> lines;
        try {
        	//replaced inputFile.absolutePath()
            lines = Files.readAllLines(Paths.get(inputFile.uri()), fileSystem.encoding());
        } catch (IOException e) {
            LOG.warn(LOG_PREFIX + "could not process file: " + inputFile.toString());
            return;
        }
        ElixirParser parser = new ElixirParser();
        parser.parse(lines);

        LOG.warn(LOG_PREFIX + "processing file: " + inputFile.toString());
        double linesOfCode = parser.getLineCount() - parser.getEmptyLineCount() - parser.getCommentLineCount();

        context.<Integer>newMeasure()
        .forMetric(CoreMetrics.LINES)
        .on(inputFile)
        .withValue(parser.getLineCount())
        .save();

        context.<Integer>newMeasure()
        .forMetric(CoreMetrics.NCLOC)
        .on(inputFile)
        .withValue((int) linesOfCode)
        .save();

        context.<Integer>newMeasure()
        .forMetric(CoreMetrics.COMMENT_LINES)
        .on(inputFile)
        .withValue((int) parser.getCommentLineCount())
        .save();

        double publicApi = parser.getPublicFunctionCount() + parser.getClassCount();
        double documentedApi = parser.getDocumentedPublicFunctionCount() + parser.getDocumentedClassCount();
        double undocumentedApi = publicApi - documentedApi;
        double documentedApiDensity = (publicApi == 0 ? 100.0 : ParsingUtils.scaleValue(documentedApi / publicApi * 100, 2));

        context.<Integer>newMeasure()
        .forMetric(CoreMetrics.PUBLIC_API)
        .on(inputFile)
        .withValue((int) publicApi)
        .save();

        context.<Integer>newMeasure()
        .forMetric(CoreMetrics.PUBLIC_UNDOCUMENTED_API)
        .on(inputFile)
        .withValue((int) undocumentedApi)
        .save();

        context.<Double>newMeasure()
        .forMetric(CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY)
        .on(inputFile)
        .withValue(documentedApiDensity)
        .save();

        int functionCount = parser.getPublicFunctionCount() + parser.getPrivateFunctionCount();

        context.<Integer>newMeasure()
        .forMetric(CoreMetrics.CLASSES)
        .on(inputFile)
        .withValue(parser.getClassCount())
        .save();

        context.<Integer>newMeasure()
        .forMetric(CoreMetrics.FUNCTIONS)
        .on(inputFile)
        .withValue(functionCount)
        .save();
    }
}
