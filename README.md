VAMIX Prototype
===============
README
------

Media Player
------------

Open media files by pressing the Open media file icon which looks like an opened folder or by pressing
Media->Open.. button from Menu.
Play and pause media files by pressing play/pause icon.
Stop media by pressing stop icon. You can resume playing same media file by pressing play. Likewise, a media file
which finished playing can be replayed by pressing play.
Fast forward or rewind media by pressing fastforward/rewind button. Resume play by pressing play button.
Mute and unmute media by pressing the speaker icon to the left of the volume slider.
Maximise audio by pressing the speaker icon to the right of the volume slider.
Adjust audio manually by dragging volume slider or clicking area in volume slider.
Adjust media time by dragging time slider or clicking area in time slider.

Download
--------

  Can be toggled on and off with the download icon. This is located next to open media file icon.
  Will prompt the user to select a folder they want to save the file into.
  Will check if the URL is a valid file with a remote media file.
  If it is not valid, it will not execute download.
  If it is valid, it will ask the user to check that it is open source. 
    Abort if not open source.
	  If open source, commence download.
	A bar showing progress will appear with the download percentage.
  Should you cancel the download, you can resume it later if you put the same link to the one you cancelled the download on.
	If the file exists locally, you will be asked if you want to override the
	current file or resume the download.

TextFilter
----------

  Has a choice between text font, text size, text colour, text position and number of seconds.
    x and y values are optional. If left empty, the natural position of the text will be near the bottom centre of
    the video.
  
  All adjustable. Can preview seperate openings and endings. Opening preview window closes 2 seconds after text 
  has finished displaying. Closing preview window closes one second after video has finished playing.
  Can save the "session" for that video by pressing the "save session" button.
  Work on other videos then come back to the video you want to continue working on, which will load the settings from when you saved the session.
  You can save the video as a new file.
	    
Extraction
----------

  Extracts AUDIO from VIDEO file.
  Must parse video in media player first.
  
  Specify extraction times:
    Put in times in the following format hh:mm:ss and click Extract button.
  Extract full video
    Click extract entire video which extracts the entire video file.

Replace Audio
-------------

  Must parse video in media player first.
  
  Choose an audio file to replace by clicking Choose File button under Replace Audio header. The selected file path
  will be shown on the JTextField.
  Replace audio by pressing Replace button and choose output filename.
  
Overlay Audio
-------------

  Must parse video in media player first.
  
  Choose an audio file to overlay by clicking Choose File button under Overlay Audio header. The selected file path
  will be shown on the JTextField.
  Overlay audio by pressing Overlay button and choose output filename.
  
