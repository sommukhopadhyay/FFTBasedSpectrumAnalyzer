package ca.uol.aig.fftpack;
/**
  * cosine FFT transform with odd wave numbers.
  * @author Baoshe Zhang
  * @author Astronomical Instrument Group of University of Lethbridge.
*/
public class RealDoubleFFT_Even_Odd extends RealDoubleFFT_Mixed
{
/**
  * <em>norm_factor</em> can be used to normalize this FFT transform. This is because
  * a call of forward transform (<em>ft</em>) followed by a call of backward transform 
  * (<em>bt</em>) will multiply the input sequence by <em>norm_factor</em>.
*/
     public double norm_factor;
     protected double wavetable[];
     protected int ndim;

/**
  * Construct a wavenumber table with size <em>n</em>.
  * The sequences with the same size can share a wavenumber table. The prime
  * factorization of <em>n</em> together with a tabulation of the trigonometric functions
  * are computed and stored.
  *
  * @param  n  the size of a real data sequence. When <em>n</em> is a multiplication of small
  * numbers(4, 2, 3, 5), this FFT transform is very efficient.
*/
     public RealDoubleFFT_Even_Odd(int n)
     {
          ndim = n;
          norm_factor = 4*n;
          if(wavetable == null || wavetable.length !=(3*ndim+15))
          {
              wavetable = new double[3*ndim + 15];
          }
          cosqi(ndim, wavetable);
     }

/**
  * Forward FFT transform of quarter wave data. It computes the coeffients in 
  * cosine series representation with only odd wave numbers.
  *
  * @param x an array which contains the sequence to be transformed. After FFT,
  * <em>x</em> contains the transform coeffients.
*/
     public void ft(double x[])
     {
         cosqf(ndim, x, wavetable);
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
         cosqb(ndim, x, wavetable);
     }

/*----------------------------------------------------------------------
   cosqf1: further processing of forward cos-FFT with odd wave numbers.
  ----------------------------------------------------------------------*/
     void cosqf1(int n, double x[], double wtable[])
     {
         int     modn, i, k;
         int     kc, np2, ns2;
         double  xim1;

         ns2=(n+1)/ 2;
         np2=n+2;
         for(k=1; k<ns2; k++)
         {
	     kc=n-k;
	     wtable[k+n]=x[k]+x[kc];
	     wtable[kc+n]=x[k]-x[kc];
         }
         modn=n%2;
         if(modn==0) wtable[ns2+n]=x[ns2]+x[ns2];
         for(k=1; k<ns2; k++)
         {
	     kc=n-k;
	     x[k]=wtable[k-1]*wtable[kc+n]+wtable[kc-1]*wtable[k+n];
	     x[kc]=wtable[k-1]*wtable[k+n]-wtable[kc-1]*wtable[kc+n];
         }
         if(modn==0) x[ns2]=wtable[ns2-1]*wtable[ns2+n];
         rfftf1(n, x, wtable, n);
         for(i=2; i<n; i+=2)
         {
	     xim1=x[i-1]-x[i];
	     x[i]=x[i-1]+x[i];
	     x[i-1]=xim1;
         }
     } 

/*----------------------------------------------------------------------
   cosqb1: further processing of backward cos-FFT with odd wave numbers.
  ----------------------------------------------------------------------*/
     void cosqb1(int n, double x[], double wtable[])
     {
         int     modn, i, k;
         int     kc, ns2;
         double  xim1;

         ns2=(n+1)/ 2;
         for(i=2; i<n; i+=2)
         {
	     xim1=x[i-1]+x[i];
	     x[i]-=x[i-1];
	     x[i-1]=xim1;
         }
         x[0]+=x[0];
         modn=n%2;
         if(modn==0) x[n-1]+=x[n-1];
         rfftb1(n, x, wtable, n);
         for(k=1; k<ns2; k++)
         {
	     kc=n-k;
	     wtable[k+n]=wtable[k-1]*x[kc]+wtable[kc-1]*x[k];
	     wtable[kc+n]=wtable[k-1]*x[k]-wtable[kc-1]*x[kc];
         }
         if(modn==0) x[ns2]=wtable[ns2-1]*(x[ns2]+x[ns2]);
         for(k=1; k<ns2; k++)
         {
	     kc=n-k;
	     x[k]=wtable[k+n]+wtable[kc+n];
	     x[kc]=wtable[k+n]-wtable[kc+n];
         }
         x[0]+=x[0];
     }

/*-----------------------------------------------
   cosqf: forward cosine FFT with odd wave numbers.
  ----------------------------------------------*/
     void cosqf(int n, double x[], final double wtable[])
     {
          final double sqrt2=1.4142135623731;
          double  tsqx;

          if(n<2)
          {
	      return;
          }
          else if(n==2)
          {
	      tsqx=sqrt2*x[1];
	      x[1]=x[0]-tsqx;
	      x[0]+=tsqx;
          }
          else
          {
              cosqf1(n, x, wtable);
          }
     } 

/*-----------------------------------------------
   cosqb: backward cosine FFT with odd wave numbers.
  ----------------------------------------------*/
     void cosqb(int n, double x[], double wtable[])
     {
          final double tsqrt2=2.82842712474619;
          double  x1;

          if(n<2)
          {
	      x[0]*=4;
          }
          else if(n==2)
          {
	      x1=4*(x[0]+x[1]);
	      x[1]=tsqrt2*(x[0]-x[1]);
	      x[0]=x1;
          }
          else
          {
              cosqb1(n, x, wtable);
          }
     }

/*-----------------------------------------------------------
   cosqi: initialization of cosine FFT with odd wave numbers.
  ----------------------------------------------------------*/
     void cosqi(int n, double wtable[])
     {
         final double pih=Math.PI/2.0D; //1.57079632679491;
         int     k;
         double  dt;
         dt=pih / (double)n;
         for(k=0; k<n; k++) wtable[k]=Math.cos((k+1)*dt);
         rffti1(n, wtable, n);
     }

}
