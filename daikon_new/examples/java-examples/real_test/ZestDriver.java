/*
 * Copyright (c) 2017-2018 The Regents of the University of California
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.berkeley.cs.jqf.fuzz.ei;

import java.io.File;

import edu.berkeley.cs.jqf.fuzz.junit.GuidedFuzzing;
import org.junit.runner.Result;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;




/**
 * Entry point for fuzzing with Zest.
 *
 * @author Rohan Padhye
 */
public class ZestDriver {

    public static void main(String[] args) {
        if (args.length == 0){
            System.err.println("Usage: java " + ZestDriver.class + " TEST_CLASS TEST_METHOD [OUTPUT_DIR [SEED_DIR | SEED_FILES...]]");
            System.exit(1);
        }


	else if (args.length == 1) {
	    
	    String testClassName  = args[0];
	    //String testMethodName = args[1];
	    
	    String outputDirectoryName = args.length > 2 ? args[2] : "fuzz-results";
	    File outputDirectory = new File(outputDirectoryName);
	    File[] seedFiles = null;

	    try {

		Class temp = Class.forName(args[0]);
		Method[] m = temp.getDeclaredMethods();


		for(int i=0; i<m.length;i++) {

		    String temp_method = m[i].toString();
		    temp_method = temp_method.substring(24);
		    String[] array = temp_method.split("\\(");
		    String testMethodName = array[0];

		    try {
			// Load the guidance
			String title = testClassName+"#"+testMethodName;
			ZestGuidance guidance = null;

			if (seedFiles == null) {
			    guidance = new ZestGuidance(title, null, outputDirectory);
			} else if (seedFiles.length == 1 && seedFiles[0].isDirectory()) {
			    guidance = new ZestGuidance(title, null, outputDirectory, seedFiles[0]);
			} else {
			    guidance = new ZestGuidance(title, null, outputDirectory, seedFiles);
			}

			// Run the Junit test

			Result res = GuidedFuzzing.run(testClassName, testMethodName, guidance, System.out);
			if (Boolean.getBoolean("jqf.logCoverage")) {
			    System.out.println(String.format("Covered %d edges.",
					guidance.getTotalCoverage().getNonZeroCount()));
			}
			if (Boolean.getBoolean("jqf.ei.EXIT_ON_CRASH") && !res.wasSuccessful()) {
			    System.exit(3);
			}

		    } catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		    }

		    System.out.println("Fuzzing " + testMethodName + "  end!!!!!!\n");
		}
	    }
	    catch (ClassNotFoundException ex) {
		ex.printStackTrace();
	    }
	}


	else {
	    //System.out.println(args[0]);
	    //System.out.println(args[1]);

	    String testClassName  = args[0];
	    String testMethodName = args[1];
	    String outputDirectoryName = args.length > 2 ? args[2] : "fuzz-results";
	    File outputDirectory = new File(outputDirectoryName);
	    File[] seedFiles = null;
	    if (args.length > 3) {
		seedFiles = new File[args.length-3];
		for (int i = 3; i < args.length; i++) {
		    seedFiles[i-3] = new File(args[i]);
		}
	    }

	    try {
		// Load the guidance
		String title = testClassName+"#"+testMethodName;
		ZestGuidance guidance = null;

		if (seedFiles == null) {
		    guidance = new ZestGuidance(title, null, outputDirectory);
		} else if (seedFiles.length == 1 && seedFiles[0].isDirectory()) {
		    guidance = new ZestGuidance(title, null, outputDirectory, seedFiles[0]);
		} else {
		    guidance = new ZestGuidance(title, null, outputDirectory, seedFiles);
		}

		// Run the Junit test

		Result res = GuidedFuzzing.run(testClassName, testMethodName, guidance, System.out);
		if (Boolean.getBoolean("jqf.logCoverage")) {
		    System.out.println(String.format("Covered %d edges.",
				guidance.getTotalCoverage().getNonZeroCount()));
		}
		if (Boolean.getBoolean("jqf.ei.EXIT_ON_CRASH") && !res.wasSuccessful()) {
		    System.exit(3);
		}

	    } catch (Exception e) {
		e.printStackTrace();
		System.exit(2);
	    }


	}

    }
}
