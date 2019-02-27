/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.test;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class MergeSort extends Task<int[]> {

    private final int[] array;

    public MergeSort(int[] array) {
        this.array = array;
    }

    @Override
    protected void start() {
    	
    	if(array.length<2){
    		//System.out.println("completing sort");
    		complete(array);
    	}
    	else{
    		int[] left= copy(array, 0, array.length/2);
    		int[] right=copy(array, array.length/2, array.length);
    		MergeSort mergeLeft=new MergeSort(left);
    		MergeSort mergeRight=new MergeSort(right);	
    		spawn(mergeLeft,mergeRight);
    		List<Task<int[]>> tasks = new ArrayList<>();
    		tasks.add(mergeRight);
    		tasks.add(mergeLeft);
    		//System.out.println("continuing sort");
    		whenResolved(tasks, ()->{
    			//System.out.println("mergesort callback is running Thread "+Thread.currentThread().getId());
    			int[] res=merge(tasks.get(0).getResult().get(),tasks.get(1).getResult().get());
    			complete(res);
    			}
    		);
    		
    		 
    	}
    }
    private int[] copy(int[] arr, int leftI, int rightI){
    	int size=rightI-leftI;
    	int[] ans=new int[size];
    	for(int i=0; i<size; i++){
    		ans[i]=arr[leftI+i];
    	}
    	return ans;
    }
    private int[] merge(int[] left, int[] right){
    	int lSize=left.length;
    	int rSize=right.length;
    	int[] ans = new int[lSize+rSize];
    	int i=0;
    	int lp=0;
    	int rp=0;
    	while(lSize>0&&rSize>0){
    		if(left[lp]>right[rp]){
    			ans[i]=right[rp];
    			rp++;
    			rSize--;
    		}
    		else{
    			ans[i]=left[lp];
    			lp++;
    			lSize--;
    		}
    		i++;
    	}
    	while(lSize>0){
    		ans[i]=left[lp];
			lp++;
			lSize--;
			i++;
    	}
    	while(rSize>0){
    		ans[i]=right[rp];
			rp++;
			rSize--;
			i++;
    	}
    	return ans;
    }
    //test to assure the array is sorted
    private static void isSorted(int[] arr){
    	boolean ans =true;
    	for(int i=1; i<arr.length; i++){
    		if(arr[i]<arr[i-1])
    			ans=false;
    	}
    	System.out.println("array is sorted="+String.valueOf(ans));
    }
    
    public static void main(String[] args) throws InterruptedException {
        WorkStealingThreadPool pool = new WorkStealingThreadPool(4);
        int n = 10; //you may check on different number of elements if you like
        int[] array = new Random().ints(n).toArray();

        MergeSort task = new MergeSort(array);

        CountDownLatch l = new CountDownLatch(1);
        pool.start();
        pool.submit(task);
        task.getResult().whenResolved(() -> {
            //warning - a large print!! - you can remove this line if you wish
            System.out.println(Arrays.toString(task.getResult().get()));
            l.countDown();
        });

        l.await();
        pool.shutdown();
    }
    //}

}
