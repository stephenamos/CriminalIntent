package com.bignerdranch.android.criminalintent;

import java.io.IOException;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CrimeCameraFragment extends Fragment {
	private static final String TAG = "CrimeCameraFragment";
	
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	
	//setType() and SURFACE_TYPE_PUSH_BUFFERS are both deprecated,
	//But are required for Camera preview to work on pre-3.0 devices.
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_crime_camera, parent);
		
		Button takePictureButton = (Button) getActivity().findViewById(R.id.crime_camera_takePictureButton);
		takePictureButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				getActivity().finish();
			}
		});
		
		mSurfaceView = (SurfaceView) view.findViewById(R.id.crime_camera_surfaceView);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		holder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				//Can no longer display on this surface; stop the preview.
				if (mCamera != null) {
					mCamera.stopPreview();
				}
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				//Tell the camera to use this surface as its preview area.
				try {
					if (mCamera != null) {
						mCamera.setPreviewDisplay(holder);
					}
				} catch (IOException exception) {
					Log.e(TAG, "Error setting up preview display", exception);
				}
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				if (mCamera == null) {
					return;
				}
				
				//The surface has changed size; update the camera preview size
				Camera.Parameters parameters = mCamera.getParameters();
				//Really: Size size = CrimeCameraFragment.this.getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height) ;
				Size size = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width, height);
				parameters.setPreviewSize(size.width, size.height);
				mCamera.setParameters(parameters);
				try {
					mCamera.startPreview();
				} catch (Exception e) {
					Log.e(TAG, "Could not start preview", e);
					mCamera.release();
					mCamera = null;
				}
			}
		});
		
		return view;
	}
	@Override
	public void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(0);
		} else {
			mCamera = Camera.open();
		}
	}
	
	//If the camera resource is not released, other applications on the phone will be unable to use the camera.
	@Override
	public void onPause() {
		super.onPause();
		
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
	
	/**
	 * A simple algorithm to get the largest valid camera preview image size available.
	 * For a more robust version, see CameraPreview.java in the ApiDemos sample app from android.
	 * 
	 * @author BigNerdRanch - Android Programming
	 */
	private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		
		for (Size size : sizes) {
			int area = size.width * size.height;
			if (area > largestArea) {
				bestSize = size;
				largestArea = area;
			}
		}
		
		return bestSize;
	}
	
	

}
