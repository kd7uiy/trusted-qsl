import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import com.kd7uiy.trustedQsl.HamBand.Band;
import com.kd7uiy.trustedQsl.QsoData;
import com.kd7uiy.trustedQsl.QsoData.Mode;
import com.kd7uiy.trustedQsl.Station;
import com.kd7uiy.trustedQsl.WriteGabbi;


public class WriteGabbiTest extends WriteGabbi {

	public WriteGabbiTest(KeyStore keystore, char[] password, String alias) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
		super(keystore,password,alias);
	}

	@Override
	protected String getApplicationName() {
		return "JavaTrustedQsl_v0.1";
	}

	@Override
	protected Station[] getStations() {
		Station station=new Station();
		station.dxcc=291;
		return new Station[]{station};
	}

	@Override
	protected QsoData[] getQsoData(Station station) {
		if (station.dxcc==291) {
			QsoData data=new QsoData();
			data.band=Band.W2M;
			data.call="KD7UIY";
			data.freq=146.580;
			data.mode=Mode.SSB;
			data.qso_dateTime=new Date();
			return new QsoData[]{data};
		} else {
			return null;
		}
	}

	@Override
	protected void publishProgress(int numComplete) {
		System.out.println("Have written "+ numComplete +" records");
		
	}
	
	//First argument is the location of your .p12 file
	public static void main(String[] args) {
		final String alias="trustedqsl user certificate";
		KeyStore keystore=WriteGabbi.getKeyStore(args[0],"");
		try {
			WriteGabbiTest writeGabbi= new WriteGabbiTest(keystore,new char[]{},alias);
			FileOutputStream output= new FileOutputStream("gabbi_output.tq8");
			writeGabbi.write(new GZIPOutputStream(output));
			output.close();
		} catch (KeyStoreException
				| NoSuchAlgorithmException | IOException | UnrecoverableEntryException e) {
			e.printStackTrace();
		}
	}

}
