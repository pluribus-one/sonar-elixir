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
 package eu.arthepsy.sonar.plugins.elixir.rule;

 
 import eu.arthepsy.sonar.plugins.elixir.language.Elixir;
 import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;


 public class ElixirQualityProfile implements BuiltInQualityProfilesDefinition {
     private static final String DEFAULT_PROFILE = "Sonar way";
//	 private static final String PROFILE_NAME = "Elixir";

    @Override
    public void define(Context context){
//    	context.profile(PROFILE_NAME, DEFAULT_PROFILE);
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(DEFAULT_PROFILE, Elixir.KEY);
        profile.setDefault(true);
        profile.done();

    }
 }
