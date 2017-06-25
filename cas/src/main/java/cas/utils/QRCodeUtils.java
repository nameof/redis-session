package cas.utils;

import java.io.IOException;
import java.io.OutputStream;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeUtils {

	public static void writeQRcodeToStream(String content, OutputStream stream) throws WriterException, IOException {
		try {
			int width = 500; // 图像宽度
			int height = 500; // 图像高度
			QRCodeWriter writer = new QRCodeWriter();
			BitMatrix m = writer.encode(content, BarcodeFormat.QR_CODE, width,
					height);
			MatrixToImageWriter.writeToStream(m, "png", stream);
		} finally {
			if (stream != null) {
				stream.flush();
				stream.close();
			}
		}
	}
}
