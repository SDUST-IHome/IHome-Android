package cn.ilell.ihome.io;
import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.StrictMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

//import android.text.InputType;
//import android.view.MotionEvent;
//import android.view.View.OnTouchListener;
@SuppressLint
({ "NewApi", "HandlerLeak" })
public class AudioClient {
	 private String IP = "192.168.0.102";  //static final 
	 private int port = 8081;
	 private Socket s;
	 private OutputStream out = null;
	 private InputStream in = null;
	 private String getmessages = "";
	 private byte buf_to_server[],buf_to_dsp[],first_buf_to_server[],first_buf_to_dsp[];
	 private int result;
	 
	    static final int frequency = 22050;  
	    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_STEREO;  
	    static final int EncodingBitRate = AudioFormat.ENCODING_PCM_16BIT;  
	    int recBufSize,playBufSize;  
	    AudioRecord audioRecord;  
	    AudioTrack audioTrack; 
	    
	   
	 public AudioClient() {


		 //以下是所加的代码
		 StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				 .detectDiskReads()
				 .detectDiskWrites()
				 .detectNetwork()
				 .penaltyLog()
				 .build());
		 StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				 .detectLeakedSqlLiteObjects()
				 .detectLeakedClosableObjects()
				 .penaltyLog()
				 .penaltyDeath()
				 .build());


		 createAudioRecord();
		 createAudioTrack();

		 buf_to_server = new byte[128];
		 buf_to_dsp = new byte[128];
		 first_buf_to_server = new byte[32768];
		 first_buf_to_dsp = new byte[32768];

	 }
	public void startAudioClient(String data_ip,int data_port) {
		IP = data_ip;
		port = data_port;
		Thread mThread = new Thread(mRunable);
		mThread.start();
	}
	private Runnable mRunable = new Runnable() {
		@Override
		public void run() {
			try {
				s = new Socket(IP,port);
				in = s.getInputStream();
				out = s.getOutputStream();
				audioRecord.startRecording();//开始录制
				audioTrack.play();//开始播放

				result = audioRecord.read(first_buf_to_server, 0, 32768); //read from audio

				if(AudioRecord.ERROR_INVALID_OPERATION != result){
					in.read(first_buf_to_dsp,0,32768);

					out.write(first_buf_to_server);	//send to socket

					audioTrack.write(first_buf_to_dsp,0, 32768);

				}
				while (true) {
					result = audioRecord.read(buf_to_server, 0, 128); //read from audio
					if(AudioRecord.ERROR_INVALID_OPERATION != result){
						in.read(buf_to_dsp,0,128);
						out.write(buf_to_server);	//send to socket
						audioTrack.write(buf_to_dsp,0, 128);
					}
				}

			}
			catch (UnknownHostException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	public void createAudioRecord(){
		recBufSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, EncodingBitRate);


		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
				channelConfiguration, EncodingBitRate, recBufSize);
	}	//创建录音

	public void createAudioTrack(){
		playBufSize=AudioTrack.getMinBufferSize(frequency,
				channelConfiguration, EncodingBitRate);


		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
				channelConfiguration, EncodingBitRate,
				playBufSize, AudioTrack.MODE_STREAM);
	}	//创建放音

}