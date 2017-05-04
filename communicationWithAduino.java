/*
 * 2017.05.03
 * Data Communication Assignment : Serial Programming
 * 2009004140 이종곤
 */
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class communicationWithAduino {

	public communicationWithAduino()
	{
		super();
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		File f = new File("var/lock");
		if(!f.exists()){
			if (!f.mkdirs())
				System.err.println("디렉토리 생성 실패");
		}
							/*
							 * gnu.io.PortInUseException: Unknown Application 에러를 발생하지 않기 위해 
							 * var/lock디렉토리를 생성
							 */


		try
		{
			(new communicationWithAduino()).connect("COM3"); //포트에 연결
			System.out.println("Connected");

		}
		catch ( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	void connect ( String portName ) throws Exception
	{
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if ( portIdentifier.isCurrentlyOwned() )
		{
			System.out.println("Error: Port is currently in use");
		}
		else
		{
			//클래스 이름을 식별자로 사용하여 포트 오픈
			CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);

			if ( commPort instanceof SerialPort )
			{
				//포트 설정(통신속도 설정. 기본 9600으로 사용)
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

				//Input,OutputStream 버퍼 생성 후 오픈
				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();

				//읽기, 쓰기 쓰레드 실행
				(new Thread(new SerialReader(in))).start();
				(new Thread(new SerialWriter(out))).start();

			}
			else
			{
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}     
	}


	/*
	 * 데이터를 수신
	 */
	public static class SerialReader implements Runnable 
	{
		InputStream in;

		public SerialReader ( InputStream in )
		{
			this.in = in;
		}

		public void run ()
		{
			byte[] buffer = new byte[1024];
			int len = -1;
			try
			{
				while ( ( len = this.in.read(buffer)) > -1 )
				{
					System.out.print(new String(buffer,0,len));
				}
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}            
		}
	}

	
	/*
	 * 데이터를 송신
	 */
	public static class SerialWriter implements Runnable 
	{
		OutputStream out;

		public SerialWriter ( OutputStream out )
		{
			this.out = out;
		}

		public void run ()
		{
			try
			{
				int c = 0;
				while ( ( c = System.in.read()) > -1 )
				{
					this.out.write(c);
				}                
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}            
		}
	}


}
