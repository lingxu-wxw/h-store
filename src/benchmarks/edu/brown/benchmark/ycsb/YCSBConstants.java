/***************************************************************************
 *  Copyright (C) 2012 by H-Store Project                                  *
 *  Brown University                                                       *
 *  Massachusetts Institute of Technology                                  *
 *  Yale University                                                        *
 *                                                                         *
 *  Coded By:  Justin A. DeBrabant (http://www.cs.brown.edu/~debrabant/)   *								   
 *                                                                         *
 *                                                                         *
 *  Permission is hereby granted, free of charge, to any person obtaining  *
 *  a copy of this software and associated documentation files (the        *
 *  "Software"), to deal in the Software without restriction, including    *
 *  without limitation the rights to use, copy, modify, merge, publish,    *
 *  distribute, sublicense, and/or sell copies of the Software, and to     *
 *  permit persons to whom the Software is furnished to do so, subject to  *
 *  the following conditions:                                              *
 *                                                                         *
 *  The above copyright notice and this permission notice shall be         *
 *  included in all copies or substantial portions of the Software.        *
 *                                                                         *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,        *
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF     *
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. *
 *  IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR      *
 *  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,  *
 *  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR  *
 *  OTHER DEALINGS IN THE SOFTWARE.                                        *
 ***************************************************************************/

package edu.brown.benchmark.ycsb;

public abstract class YCSBConstants {

	//public static final int NUM_RECORDS = 2684354; // 128 MB
	//public static final int NUM_RECORDS = 5368709; // 256 MB
	//public static final int NUM_RECORDS = 41943; // 2 MB
	//public static final int NUM_RECORDS = 21474836; // 1 GB
    public static final int NUM_RECORDS = 10737418; // .5 GB

	public static final int HOT_DATA_WORKLOAD_SKEW = 90;
	public static final int HOT_DATA_SIZE = 10;
	
	public static final int WARM_DATA_SIZE = 0; 
	public static final int WARM_DATA_WORKLOAD_SKEW = 0; 

	
	public static final String TABLE_NAME = "USERTABLE"; 
    public static final int NUM_COLUMNS = 11; 
	public static final int COLUMN_LENGTH = 100;
	
	public static final int BATCH_SIZE = 10000; 
	public static final int MAX_SCAN = 1000; 
	
	// Transaction frequencies as specified in YCSB
	public static final int FREQUENCY_INSERT_RECORD = 0; 
	public static final int FREQUENCY_DELETE_RECORD = 0;
	public static final int FREQUENCY_READ_RECORD = 90;
	public static final int FREQUENCY_SCAN_RECORD = 0;
	public static final int FREQUENCY_UPDATE_RECORD = 10;
}
