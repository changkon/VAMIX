package worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.JOptionPane;

import java.lang.Exception;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/** Handles the download process and the threading
 * 
 */

public class DownloadWorker extends SwingWorker<Integer,Integer>{
	
	private ProcessBuilder builder;
	private Process process;
	private ProgressMonitor monitor;
	
	public DownloadWorker(String URL, ProgressMonitor monitor, String path){
		builder = new ProcessBuilder("/bin/bash","-c","wget -c " + URL + " -P " + path);
		this.monitor = monitor;
				
	}
	//calculation heavy processes to be done on background thread
	@SuppressWarnings("unused")
	@Override
	protected Integer doInBackground() throws Exception{							
		Integer exitStatus = -1;
		//show the progress bar and the cancel button
		
		//redirect stderror to stdout and process it below
		String line = null;							
		String stdOutput = null;
		builder.redirectErrorStream(true);
		
		//start the process
		process = builder.start();
		InputStream out = process.getInputStream();
		InputStream err = process.getErrorStream();
		BufferedReader stdout = new BufferedReader(new InputStreamReader(out));
		BufferedReader stderr = new BufferedReader(new InputStreamReader(err));
				
		
			//process the stdout to %			
			while ((stdOutput = stdout.readLine()) != null && !isCancelled() && !monitor.isCanceled()) {
				line = stdOutput;
				String[] teemp = stdOutput.split("%");
				String temp = teemp[0];
				String substring = ( temp.length() > 2 ) ? temp.substring(temp.length() - 2) : temp;
				substring = substring.replaceAll("\\s+","");
					if(checkInteger(substring)){
						Integer perc = (Integer) Integer.parseInt(substring);
						if(perc != 88){	
							publish(perc);
						}
					}	
			}
			//if the swingworker is cancelled destroy the process
			//hide the button and the progress bar
			if(isCancelled() || monitor.isCanceled()){
				process.destroy();
				monitor.close();
			}
		
		//wait for the process to finish
		process.waitFor();	
		//process the exit status of WGET
		exitStatus = process.exitValue();
		return exitStatus;
				
	}
	
	@Override
	protected void done(){
		monitor.close();
		try{//if successful, log it
			if(get() == 0){
				JOptionPane.showMessageDialog(null, "Successful Download! Please press okay!");
				//PrintWriter writer = null;
			}
			else{//if not successful, return the error code decomposition
				if(get() == 1){
					JOptionPane.showMessageDialog(null, "Generic Error Code. Press okay!");
				}else if(get() == 2){
					JOptionPane.showMessageDialog(null, "Prase Error. Press okay!");
				}else if(get() == 3){
					JOptionPane.showMessageDialog(null, "File IO Error. Press okay!");
				}else if(get() == 4){
					JOptionPane.showMessageDialog(null, "Make sure your URL is correct. Press okay!");
				}else if(get() == 5){
					JOptionPane.showMessageDialog(null, "SSL verification failure. Press okay!");
				}else if(get() == 6){
					JOptionPane.showMessageDialog(null, "Username/password authentication failure. Press okay!");
				}else if(get() == 7){
					JOptionPane.showMessageDialog(null, "Protocol Errors. Press okay!");
				}else if(get() == 8){
					JOptionPane.showMessageDialog(null, "Server issued an error response. Press okay!");
				}else{
					JOptionPane.showMessageDialog(null, "Download did not complete! Type the same URL to resume download!");
				}
			}
		}catch(InterruptedException ie){
			ie.printStackTrace();
		}catch(ExecutionException ee){
			ee.printStackTrace();
		}catch (CancellationException ce){
			//if cancelled, tell the user
			JOptionPane.showMessageDialog(null, "Cancelled! Type the same URL to resume download!");
			//ce.printStackTrace();
		}
	}
	
	/** Process the ProgressMonitor
	 * 
	 */
	@Override
	protected void process(List<Integer> chunks) {
		if (!isDone()) {
			for (Integer element : chunks) {
				String format = String.format("Completed : %2d%%", (int)((double)element));
				monitor.setNote(format);
				monitor.setProgress(element);
			}
		}
	}
	
	//check if the string is integer
	protected boolean checkInteger(String s){
		try { 
			Integer.parseInt(s); 
		} catch(NumberFormatException e) { 
			return false; 
		}
		return true;				
	}
}
