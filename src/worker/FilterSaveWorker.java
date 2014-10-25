package worker;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ProgressMonitor;

import operation.MediaTimer;

/**
 * Encodes filter options to file. Progress is shown on progress monitor.
 */

public class FilterSaveWorker extends DefaultWorker {
	private String inputFile, outputFile;
	private ArrayList<Object[]> textList;
	
	public FilterSaveWorker(String inputFile, String outputFile, ArrayList<Object[]> textList, ProgressMonitor monitor) {
		
		super(monitor);
		
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.textList = textList;
		
		initialiseVariables();
	}

	@Override
	protected String getCommand() {
		//detects the number of seconds to display for and what to display
		StringBuilder command = new StringBuilder();
		
		command.append("avconv -i \'" + inputFile + "\' -c:a copy -vf drawtext=\"fontfile=");
		
		for (Iterator<Object[]> iter = textList.iterator(); iter.hasNext();) {
			Object[] i = iter.next();
			
			int visibleTime = MediaTimer.getSeconds(i[0].toString()) + MediaTimer.getDifferenceInTimeSeconds(i[0].toString(), i[1].toString());
			
			command.append(i[3] + ": fontsize=");
			command.append(i[4] + ": fontcolor=");
			command.append(i[5] + ": x=");
			command.append(i[6] + ": y=");
			command.append(i[7] + ": text=\'");
			command.append(i[2] + "\': draw=\'gt(t," + MediaTimer.getSeconds(i[0].toString()) + ")*lt(t,");
			command.append(visibleTime + ")\'");
			
			if (iter.hasNext()) {
				command.append(":,drawtext=fontfile=");
			}
		}
		
		command.append("\" -y \'" + outputFile + "\'");
		System.out.println(command.toString());
		return command.toString();
	}
	
	@Override
	protected String getSuccessMessage() {
		return "Filtering has completed";
	}

	@Override
	protected String getCancelMesssage() {
		return "Filtering was interrupted";
	}
}
