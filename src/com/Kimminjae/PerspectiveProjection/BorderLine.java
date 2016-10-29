package com.Kimminjae.PerspectiveProjection;

import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;


public class BorderLine {
	// init the top and bottm pointer
	int top = 0;
	int bottom = 0;
	IplImage src;
	
	// keep moving while move is false
	boolean move = false;

	public BorderLine(int top, int bottom, IplImage src) {

		this.top = top;
		this.bottom = bottom;
		this.src = src;
	}

	public void moveAndDraw(IplImage src, IplImage dst, int i) {
	
				CvPoint pt0 = cvPoint( top + i,0);
				CvPoint pt1 = cvPoint(bottom + i, dst.width());
				cvLine(dst, pt0, pt1, CV_RGB(255, 0, 0), 2, 8, 0);
				
				
	}

}
