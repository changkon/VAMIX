VAMIX Prototype
===============
README
------

Running VAMIX
---------------

Through the terminal:
java -jar filename.jar

Through Eclipse:
Import files to eclipse project and add src, help and res to be source folders. Add miglayout-4.0-swing.jar and vlcj-3.0.1.jar to Build Path.

Media Player
------------

Open media files by pressing the Open media file icon which looks like an opened folder or by pressing
Media->Open.. button from Menu.
Play and pause media files by pressing play/pause icon.
Stop media by pressing stop icon. You can resume playing same media file by pressing play. Likewise, a media file which finished playing can be replayed by pressing play.
Fast forward or rewind media by pressing fastforward/rewind button. Resume play by pressing play button.
Mute and unmute media by pressing the speaker icon to the left of the volume slider.
Maximise audio by pressing the speaker icon to the right of the volume slider.
Adjust audio manually by dragging volume slider or clicking area in volume slider.
Adjust media time by dragging time slider or clicking area in time slider.
Double click media player component to enter full screen.
The fullscreen button toggles fullscreen. If fullscreen is not supported, a message will appear.
During fullscreen, the playback panel appears whenever mouse is moved and disappears after 3 seconds of idle.
Exiting fullscreen can be achieved by pressing esc and playing/pausing by pressing space during fullscreen mode.

Download
--------

Enter url to textfield and press download button to start download. Specify file location and overwrite if necessary.
Cancel download by selecting the desired download and press cancel button. Downloading can be resumed if you cancel download and start again by overwriting it. If it's the same download, it will continue from before. Before downloading, it checks if it's a valid link. If it's not a valid link, it tells user.

TextFilter
----------

Has a choice between text font, text size, text colour, text position. The duration of the text edit is determined by specifying the start and end times. The start and end button can be pressed to get the current media time. The text to be displayed must be added to the text box.
  
Add the data to the table by pressing Add. Edit the data by selecting the row to edit on the table and press edit. Delete data by selecting the row to delete and press the Delete button.
Preview text edit by selecting the row to preview and press the preview button. Save text edit to the video by pressing the Save Video button and specify output name and file type.
Save current work by pressing Save Work button and save the current session into a text file. Load previous work by pressing Load Work button and choose an appropriate text file.
  
Fade Filter
-----------
  
Specify start and end time for fade effects. Choose fade type and then press Add to add the fade effect data to the table. Edit fade data by selecting the row to edit and press Edit. Delete fade data by selecting the row to delete and press delete. Preview fade effect by selecting the fade effect to preview on the table and press Preview Fade. Save the fade effects by pressing Save Fade and specify output filename and type.
Save work by pressing Save Work to save the current session. Load work by pressing Load Work to load previous fade effect sessions.
  
Subtitle
--------

Import subtitles to the table by pressing the import button. Specify the start and end times for the subtitle to appear in the video. Put the desired text into the text box. Add subtitles by pressing Add. Edit subtitles in the table by selecting the row in the table and press Edit. Delete subtitle data by selecting the row in the table and press Delete.
Save the subtitle in the table to a srt file by pressing Save Subtitle.
Place subtitle to a video file by pressing Add Subtitle to Video button and choose a subtitle file and then specify the output name and file type.
	    
Extraction
----------

Extracts AUDIO from MEDIA file.
Must be playing a video in media player first.
  
Specify extraction times:
  Put in times in the following format hh:mm:ss and click Extract button.
Extract full audio
  Click extract entire file which extracts audio from the media file.

Replace Audio
-------------

Must be playing a video in media player first.
 
Choose an audio file to replace by clicking Choose File button under Replace Audio header. The selected file path will be shown on the JTextField.
Replace audio by pressing Replace button and choose output filename.
  
Overlay Audio
-------------

Must be playing a video in media player first.
  
Choose an audio file to overlay by clicking Choose File button under Overlay Audio header. The selected file path will be shown on the JTextField.
Overlay audio by pressing Overlay button and choose output filename.
  
Add Audio Track
---------------

Must be playing a video in media player first.
  
Choose an audio file to add to video file by clicking Choose File button. The selected file path will be shown on the JTextField.
Add audio track by clicking Add Track button and choose output filename.
  
Change Volume
-------------

Must be playing media player with a media file which has an audio track.
  
Select a multiplier using the JSpinner which increments/decrements the value by 0.1. The selection values are between 0 - 5. eg. half the volume of the original should be set to 0.5
  
Change volume by clicking Change Volume button and choose output filename.
  
Known Bugs
----------

  1. FullScreen is not perfect. If the media player is paused during the main menu entering fullscreen will play video instead of remaining paused. Mute icons and keeping mute is not implemented at the moment.
  2. For some progress monitors such as during Change Volume, the progress bar does not update when the input file is an audio file. This is because of an error during avconv command. However, the output file still seems to work as expected.
  3. For previewing fade filters, preview doesn't work properly. If the fade filter effect is not near the start of the video, the video remains black in the preview window. If the user keeps playing the preview window until the end of the video, the window freezes and the user is unabe to close the window unless VAMIX is closed.

More Information
----------------

For more information, please see the User Manual
