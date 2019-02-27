package bgu.spl.a2.sim;

import com.google.gson.annotations.*;

import bgu.spl.a2.sim.conf.ManufactoringPlan;
public class JsonReader {
@SerializedName("plans")
public ManufactoringPlan[] plans;


@SerializedName("threads")
int threads;


@SerializedName("tools")
public Tools[] tools;


@SerializedName("waves")
public Wave[][] waves;
		
	}
	


