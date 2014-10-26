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
  Double click media player component to enter full screen.
  The fullscreen button toggles fullscreen. If fullscreen is not supported, a message will appear.
  During fullscreen, the playback panel appears whenever mouse is moved and disappears after 3 seconds of idle.
  Exiting fullscreen can be achieved by pressing esc and playing/pausing by pressing space during fullscreen mode.

Download
--------

  Enter url to textfield and press download button to start download. Specify file location and overwrite if
  necessary.
  Cancel download by selecting the desired download and press cancel button. Downloading can be resumed if you cancel
  download and start again by overwriting it. If it's the same download, it will continue from before. Before
  downloading, it checks if it's a valid link. If it's not a valid link, it tells user.

TextFilter
----------

  Has a choice between text font, text size, text colour, text position and number of seconds.
    x and y values are optional. If left empty, the natural position of the text will be near the bottom centre of
    the video.
  
  All adjustable. Can preview seperate openings and endings. Opening preview window closes 2 seconds after text 
  has finished displaying. Closing preview window closes one second after video has finished playing.
  Can save the "session" for that video by pressing the "save session" button.
  Work on other videos then come back to the video you want to continue working on, which will load the settings from
  when you saved the session.
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
  
Add Audio Track
---------------

  Must parse video in media player first.
  
  Choose an audio file to add to video file by clicking Choose File button. The selected file path will be shown
  on the JTextField.
  Add audio track by clicking Add Track button and choose output filename.
  
Change Volume
-------------

  Must parse media player with a media file which has an audio track.
  
  Select a multiplier using the JSpinner which increments/decrements the value by 0.1. The selection values are
  between 0 - 5. eg. half the volume of the original should be set to 0.5
  
  Change volume by clicking Change Volume button and choose output filename.
  
Known Bugs
----------

  1. FullScreen is not perfect. If the media player is paused during the main menu entering fullscreen will play
     video instead of remaining paused. Mute icons and keeping mute is not implemented at the moment.
  2. For some progress monitors such as during Change Volume, the progress bar does not update when the input file
     is an audio file. This is because of an error during avconv command. However, the output file still seems to
     work as expected.

