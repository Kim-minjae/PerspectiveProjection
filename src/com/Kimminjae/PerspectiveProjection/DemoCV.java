package com.Kimminjae.PerspectiveProjection;

import static org.bytedeco.javacpp.helper.opencv_core.CV_RGB;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;

import static org.bytedeco.javacpp.opencv_imgcodecs.*;

import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.indexer.UByteBufferIndexer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter.ToIplImage;

public class DemoCV {
	static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

	public static void main(String[] args) throws Exception {

		CvMemStorage storage = cvCreateMemStorage(10000);
		CvSeq lines = new CvSeq();
		@SuppressWarnings("resource")
		CvSeq squares1 = new CvSeq();

		FrameGrabber grabber = FrameGrabber.createDefault(0);
		grabber.start();

		IplImage grabbedImage = converter.convert(grabber.grab());
		Mat grabbedImageMat = converter.convertToMat(grabber.grab());
		int i = 0;

		// savepoint of the longest i
		int borderpoint = 0;
		int ancho = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        int alto = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
		CanvasFrame frame = new CanvasFrame("originalFrame", CanvasFrame.getDefaultGamma() / grabber.getGamma());

		while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
			
			
			IplImage gray = cvCreateImage(cvGetSize(grabbedImage), 8, 1);
			cvCvtColor(grabbedImage, gray, CV_BGR2GRAY);
			cvCanny(gray, gray, 50, 100, 3);
			cvCvtColor(gray, grabbedImage, CV_GRAY2BGR);
			BorderLine liner = new BorderLine(0, 0, grabbedImage);
			liner.moveAndDraw(grabbedImage, grabbedImage, i);
			
			
			if(i == grabbedImage.width())
			{
				i=0;
			}
			i++;
			
			boderDetect(grabbedImage);
		//	FindAndDrawLines(gray, lines, gray, grabbedImage);
			Frame rotatedFrame = converter.convert(grabbedImage);
			frame.setCanvasSize(ancho*1920/3440, alto*1080/1440); 
			frame.waitKey(30);

			frame.showImage(rotatedFrame);

		}
		frame.dispose();

		grabber.stop();
	}

	public static void captureFrame() {
		OpenCVFrameGrabber grabber2 = new OpenCVFrameGrabber(0);
		try {
			grabber2.start();
			IplImage img = converter.convert(grabber2.grab());

			if (img != null) {
				cvSaveImage("capture.jpg", img);
			}
		} catch (Exception ae) {
			ae.printStackTrace();
		}
	}

	public static void boderDetect(IplImage src) {
		// the longest line is the border of projected image

		// preprocessing for red line detecting
		//IplImage imageDst = IplImage.create(src.width(), src.height(), IPL_DEPTH_8U,3);
		//CvMat mtx = CvMat.createHeader(src.height(), src.width(), CV_32FC1);
		//UByteBufferIndexer sI = mtx.createIndexer();
		
		
		for (int i = 0; i < src.width(); i++) {
			
			CvScalar s = cvGet2D(src,0,i);
			
			if (s.val(2) < 100 && s.val(1) > 50 && s.val(0) > 50  ) {
				
				CvPoint pt0 = cvPoint(i, 0);
				CvPoint pt1 = cvPoint(i, src.width());
				cvLine(src, pt0, pt1, CV_RGB(0, 0, 0));
				// indexer.put(row, col, i, 255.toByte)

			}
		}
	}

	public static void FindAndDrawLines(IplImage src, CvSeq lines, IplImage dst, IplImage colorDst) {

		CvMemStorage storage = cvCreateMemStorage(0);

		if (src == null) {
			System.out.println("Couldn't load source image.");
			return;
		}
		// cvCanny(src, dst, 50, 100, 3);
		// cvCvtColor(dst, colorDst, CV_GRAY2BGR);

		lines = cvHoughLines2(dst, storage, CV_HOUGH_PROBABILISTIC, 1, Math.PI / 180, 40, 50, 10, 0, CV_PI);

		for (int i = 0; i < lines.total(); i++) {
			// Based on JavaCPP, the equivalent of the C code:
			// CvPoint* line = (CvPoint*)cvGetSeqElem(lines,i);
			// CvPoint first=line[0], second=line[1]
			// is:
			Pointer line = cvGetSeqElem(lines, i);
			CvPoint pt1 = new CvPoint(line).position(0);
			CvPoint pt2 = new CvPoint(line).position(1);

			cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 3, CV_AA, 0); // draw
																		// the
																		// segment
																		// on
																		// the
																		// image
		}
	}

	public static double length(CvPoint pt1, CvPoint pt2) {
		double dx1 = pt1.x() - pt2.x();
		double dy1 = pt1.y() - pt2.y();

		return Math.sqrt(dx1 * dx1 + dy1 * dy1 + 1e-10);
	}
}