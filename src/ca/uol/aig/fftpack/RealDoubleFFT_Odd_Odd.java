package ca.uol.aig.fftpack;
/**
  * sine FFT transform with odd wave numbers.
  * @author Baoshe Zhang
  * @author Astronomical Instrument Group of University of Lethbridge.
*/
public class RealDoubleFFT_Odd_Odd extends RealDoubleFFT_Even_Odd
{
/**
  * <em>norm_factor</em> can be used to normalize this FFT transform. This is because
  * a call of forward transform (<em>ft</em>) followed by a call of backward transform
  * (<em>bt</em>) will multiply the input sequence by <em>norm_factor</em>.
*/

/**
  * Construct a wavenumber table with size n.
  * The sequences with the same size can share a wavenumber table. The prime
  * factorization of <em>n</em> together with a tabulation of the trigonometric functions
  * are computed and stored.
  *
  * @param  n  the size of a real data sequence. When <em>n</em> is a multiplication of small
  * numbers (4, 2, 3, 5), this FFT transform is very efficient.
*/
     public RealDoubleFFT_Odd_Odd(int n)
     {
          super(n);
     }

/**
  * Forward FFT transform of quarter wave data. It computes the coeffients in 
  * sine series representation with only odd wave numbers.
  * 
  * @param x an array which contains the sequence to be transformed. After FFT,
  * <em>x</em> contains the transform coeffients.
*/
     public void ft(double x[])
     {
         sinqf(ndim, x, wavetable);
     }

/**
  * Backward FFT transform of quarter wave data. It is the unnormalized inverse transform
  * of <em>ft</em>. 
  *
  * @param x an array which contains the sequence to be tranformed. After FFT, <em>x</em> contains
  * the transform coeffients.
*/
     public void bt(double x[])
     {
         sinqb(ndim, x, wavetable);
     }

/*-----------------------------------------------
   sinqf: forward sine FFT with odd wave numbers.
  ----------------------------------------------*/
     void sinqf(int n, double x[], double wtable[])
     {
         int     k;
         double  xhold;
         int     kc, ns2;

         if(n==1) return;
         ns2=n / 2;
         for(k=0; k<ns2; k++)
         {
	      kc=n-k-1;
	      xhold=x[k];
	      x[k]=x[kc];
	      x[kc]=xhold;
         }
         cosqf(n, x, wtable);
         for(k=1; k<n; k+=2) x[k]=-x[k];
     } 

/*-----------------------------------------------
   sinqb: backward sine FFT with odd wave numbers.
  ----------------------------------------------*/
     void sinqb(int n, double x[], double wtable[])
     {
          int     k;
          double  xhold;
          int     kc, ns2;

          if(n<=1)
          {
	      x[0]*=4;
	      return;
          }
          ns2=n / 2;
          for(k=1; k<n; k+=2) x[k]=-x[k];
          cosqb(n, x, wtable);
          for(k=0; k<ns2; k++)
          {
	      kc=n-k-1;
	      xhold=x[k];
	      x[k]=x[kc];
	      x[kc]=xhold;
          }
     } 

/*
     void sinqi(int n, double wtable[])
     {
          cosqi(n, wtable);
     }
*/
}
