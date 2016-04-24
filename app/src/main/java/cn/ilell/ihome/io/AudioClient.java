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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

//import android.text.InputType;
//import android.view.MotionEvent;
//import android.view.View.OnTouchListener;
@SuppressLint
({ "NewApi", "HandlerLeak" })
public class AudioClient {

	private String ser_ip = "192.168.0.102";  //static final
	private DatagramSocket socket = null;
	//使用InetAddress(Inet4Address).getByName把IP地址转换为网络地址
	private InetAddress serverAddress = null;
	private int ser_port = 8081;
	private int listen_port = 18082;
	private long currentID;	//当前接收到的包的ID

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


	}

	public void startAudioClient(String data_ip,int data_port) {
		//IP = data_ip;
		//port = data_port;
		//创建DatagramSocket对象并指定一个端口号，注意，如果客户端需要接收服务器的返回数据,
		//还需要使用这个端口号来receive，所以一定要记住
		try {
			socket = new DatagramSocket(listen_port);
			serverAddress = InetAddress.getByName(ser_ip);
		}  catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		Thread mThread_sock_to_audio = new Thread(mRunable_sock_to_audio);
		mThread_sock_to_audio.start();
		Thread mThread_audio_to_sock = new Thread(mRunable_audio_to_sock);
		mThread_audio_to_sock.start();
	}

	private Runnable mRunable_audio_to_sock = new Runnable() {
		@Override
		public void run() {
			Package pack = new Package();
			audioRecord.startRecording();//开始录制
			while (true) {
				result = audioRecord.read(buf_to_server, 0, Package.BUFLEN); //read from audio
				if(AudioRecord.ERROR_INVALID_OPERATION != result) {
					pack.setData(buf_to_server);
					pack.setId(pack.getId()+1);
					//System.out.println("audio to sock:"+pack.getId());
					//创建一个DatagramPacket对象，用于发送数据。
					//参数一：要发送的数据  参数二：数据的长度  参数三：服务端的网络地址  参数四：服务器端端口号
					DatagramPacket dataPacket = new DatagramPacket(pack.getPackageByte(), pack.getPackageByte().length,
							serverAddress, ser_port);
					try {
						socket.send(dataPacket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};
	private Runnable mRunable_sock_to_audio = new Runnable() {
		@Override
		public void run() {
			Package pack = new Package();
			byte[] buf_sock = new byte[Package.BUFLEN+8];
			audioTrack.play();//开始播放
			while (true) {
				//参数一:要接受的data 参数二：data的长度
				DatagramPacket packet = new DatagramPacket(buf_sock, buf_sock.length);
				try {
					socket.receive(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
				pack.analysisBuf(buf_sock);
				//System.out.println("sock to audio:"+pack.getId());
				audioTrack.write(pack.getData(),0, Package.BUFLEN);
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

	public void onDestory() {
		socket.close();
	}

}