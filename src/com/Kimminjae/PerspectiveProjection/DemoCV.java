package com.Kimminjae.PerspectiveProjection;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;

import static org.bytedeco.javacpp.opencv_imgcodecs.*;

import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;



public class DemoCV {
	
    public static void main(String[] args) throws Exception {  
    	
    	CvMemStorage storage = null;
    	CvSeq lines = new CvSeq();
    	CvSeq squares1 = cvCreateSeq(0, Loader.sizeof(CvSeq.class), Loader.sizeof(CvPoint.class), storage);

    	square detector = new square();
    	
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();

        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

       
        IplImage grabbedImage = converter.convert(grabber.grab());
        int width  = grabbedImage.width();
        int height = grabbedImage.height();

        CanvasFrame frame = new CanvasFrame("My Face", CanvasFrame.getDefaultGamma()/grabber.getGamma());

        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
        	
        	IplImage gray = cvCreateImage(cvGetSize(grabbedImage), 8, 1);
        	cvCvtColor(grabbedImage, gray, CV_BGR2GRAY);
        	FindAndDrawLines(grabbedImage,lines,gray,grabbedImage);
        	
        	squares1 = detector.findSquares4(grabbedImage, storage);
        	detector.drawSquares(grabbedImage,squares1);
        	
            Frame rotatedFrame = converter.convert(grabbedImage);
            
            
            frame.showImage(rotatedFrame);
        }
        frame.dispose();
        grabber.stop();
    }
    
	public static void FindAndDrawLines(IplImage src, CvSeq lines ,IplImage dst, IplImage colorDst)
    {
		
    	CvMemStorage storage = cvCreateMemStorage(0);
    
        if (src == null) {
            System.out.println("Couldn't load source image.");
            return;
        }

        cvCanny(src, dst, 50, 100, 3);
        cvCvtColor(dst, colorDst, CV_GRAY2BGR);
        
        lines = cvHoughLines2(dst, storage, CV_HOUGH_PROBABILISTIC, 1, Math.PI / 180, 40, 50, 10, 0, CV_PI);

        for (int i = 0; i < lines.total(); i++) {
            // Based on JavaCPP, the equivalent of the C code:
            // CvPoint* line = (CvPoint*)cvGetSeqElem(lines,i);
            // CvPoint first=line[0], second=line[1]
            // is:
            Pointer line = cvGetSeqElem(lines, i);
            CvPoint pt1  = new CvPoint(line).position(0);
            CvPoint pt2  = new CvPoint(line).position(1);

            cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 3, CV_AA, 0); // draw the segment on the image
        }
        
//        lines = cvHoughLines2(dst, storage, CV_HOUGH_MULTI_SCALE, 1, Math.PI / 180, 40, 1, 1, 0, CV_PI);
//        for (int i = 0; i < lines.total(); i++) {
//            CvPoint2D32f point = new CvPoint2D32f(cvGetSeqElem(lines, i));
//
//            float rho=point.x();
//            float theta=point.y();
//
//            double a = Math.cos((double) theta), b = Math.sin((double) theta);
//            double x0 = a * rho, y0 = b * rho;
//            CvPoint pt1 = cvPoint((int) Math.round(x0 + 1000 * (-b)), (int) Math.round(y0 + 1000 * (a))), pt2 = cvPoint((int) Math.round(x0 - 1000 * (-b)), (int) Math.round(y0 - 1000 * (a)));
//            
//            cvLine(colorDst, pt1, pt2, CV_RGB(0, 255, 0), 3, CV_AA, 0);
//        }
        
//        lines = cvHoughLines2(dst, storage, CV_HOUGH_STANDARD, 1, Math.PI / 180, 90, 0, 0, 0, CV_PI);
//        for (int i = 0; i < lines.total(); i++) {
//            CvPoint2D32f point = new CvPoint2D32f(cvGetSeqElem(lines, i));
//
//            float rho=point.x();
//            float theta=point.y();
//
//            double a = Math.cos((double) theta), b = Math.sin((double) theta);
//            double x0 = a * rho, y0 = b * rho;
//            CvPoint pt1 = cvPoint((int) Math.round(x0 + 1000 * (-b)), (int) Math.round(y0 + 1000 * (a))), pt2 = cvPoint((int) Math.round(x0 - 1000 * (-b)), (int) Math.round(y0 - 1000 * (a)));
//            System.out.println("Line spotted: ");
//            System.out.println("\t rho= " + rho);
//            System.out.println("\t theta= " + theta);
//            cvLine(colorDst, pt1, pt2, CV_RGB(255, 0, 0), 3, CV_AA, 0);
//        }
    }
	
	public static void FindSquers(IplImage src, IplImage dst, CvSeq squares)
	{
		int thresh = 50;
		CvMemStorage storage = null;
		
	}
	public static  double angle(CvPoint pt1, CvPoint pt2, CvPoint pt0) {
        double dx1 = pt1.x() - pt0.x();
        double dy1 = pt1.y() - pt0.y();
        double dx2 = pt2.x() - pt0.x();
        double dy2 = pt2.y() - pt0.y();

        return (dx1*dx2 + dy1*dy2) / Math.sqrt((dx1*dx1 + dy1*dy1) * (dx2*dx2 + dy2*dy2) + 1e-10);
    }
	
}