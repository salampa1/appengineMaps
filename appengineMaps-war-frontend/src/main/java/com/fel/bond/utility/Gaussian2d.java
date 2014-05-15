package com.fel.bond.utility;
import javax.vecmath.Point2d;
import weka.core.matrix.Matrix;

/**
 * Class representing Gaussian distribution.
 * Modified version of the original taken from the AgentC Project:
 * http://agents.cz/projects/AgentC
 * 
 */
public class Gaussian2d {

	private Matrix mi;
	private Matrix sigma;
	private Matrix precision;
	private Matrix rotation;
	private double a;
	private double b;
	private double c;
	private double multiplier = 1;
    
    private double theta;
    

	public Gaussian2d(double mi, double sigma) {
		this(mi, mi, sigma, sigma);

	}

	public Gaussian2d(double mix, double miy, double sigmax, double sigmay,
			double theta) {
        this.theta = theta;
        
		a = Math.pow(Math.cos(theta), 2) / 2 / sigmax / sigmax
				+ Math.pow(Math.sin(theta), 2) / 2 / sigmay / sigmay;
		b = -Math.sin(2 * theta) / 4 / sigmax / sigmax + Math.sin(2 * theta)
				/ 4 / sigmay / sigmay;
		c = Math.pow(Math.sin(theta), 2) / 2 / sigmax / sigmax
				+ Math.pow(Math.cos(theta), 2) / 2 / sigmay / sigmay;

		this.mi = new Matrix(1, 2);
		this.mi.set(0, 0, mix);
		this.mi.set(0, 1, miy);
		this.sigma = new Matrix(2, 2);

		rotation = new Matrix(2, 2);
		rotation.set(0, 0, Math.cos(theta));
		rotation.set(0, 1, -Math.sin(theta));
		rotation.set(1, 0, Math.sin(theta));
		rotation.set(1, 1, Math.cos(theta));

		this.sigma.set(0, 0, sigmax);
		this.sigma.set(1, 1, sigmay);
		precision = new Matrix(2, 2);
		precision.set(0, 0, a);
		precision.set(0, 1, b);
		precision.set(1, 0, b);
		precision.set(1, 1, c);
	}

	public Gaussian2d(double mix, double miy, double sigmax, double sigmay) {
		this(mix, miy, sigmax, sigmay, 0);

		this.mi = new Matrix(1,2);
		 this.mi.set(0, 0, mix);
		 this.mi.set(0, 1, miy);
		 this.sigma = new Matrix(2,2);
		 this.sigma.set(0, 0, sigmax);
		 this.sigma.set(1, 1, sigmay);
		
	}

	public Gaussian2d(Point2d pos, double sigma) {
		this(pos.x, pos.y, sigma, sigma, 0);
	}
    
    public double getTheta() {
        return theta;
    }

	public double getMix() {
		return mi.get(0, 0);
	}

	public double getMiy() {
		return mi.get(0, 1);
	}

	public double getSigmax() {
		return sigma.get(0, 0);
	}

	public double getSigmay() {
		return sigma.get(1, 1);
	}

	public void setMi(double mix, double miy) {
		this.mi.set(0, 0, mix);
		this.mi.set(0, 1, miy);
	}

	public double getValueAt(double u, double v) {
		Matrix in = new Matrix(1, 2);
		in.set(0, 0, u);
		in.set(0, 1, v);
		double coef = 1;
		double exponent = -(a * Math.pow((u - mi.get(0, 0)), 2) + 2 * b
				* (u - mi.get(0, 0)) * (v - mi.get(0, 1)) + c
				* Math.pow((v - mi.get(0, 1)), 2));
		return multiplier * coef * Math.exp(exponent);
	}

	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}
	

	
	public static void main(String[] args) {
        double mix = 0;
        double miy = 0;
        double sigmax = 1;
        double sigmay = 1;
        double theta = Math.toRadians(0); // clockwise rotation
        
		Gaussian2d gauss = new Gaussian2d(mix,miy,sigmax,sigmay,theta);
        System.out.println(gauss.getValueAt(3, 0));
	}
}


