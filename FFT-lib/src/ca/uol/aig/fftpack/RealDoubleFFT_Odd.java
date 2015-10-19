package ca.uol.aig.fftpack;
/**
  * sine FFT transform of a real odd sequence.
  * @author Baoshe Zhang
  * @author Astronomical Instrument Group of University of Lethbridge.
*/
public class RealDoubleFFT_Odd extends RealDoubleFFT_Mixed
{
/**
  * <em>norm_factor</em> can be used to normalize this FFT transform. This is because
  * a call of forward transform (<em>ft</em>) followed by a call of backward 
  * transform (<em>bt</em>) will multiply the input sequence by <em>norm_factor</em>.
*/
     public double norm_factor;
     private double wavetable[];
     private int ndim;


/**
  * Construct a wavenumber table with size <em>n</em>.
  * The sequences with the same size can share a wavenumber table. The prime
  * factorization of <em>n</em> together with a tabulation of the trigonometric functions
  * are computed and stored.
  *
  * @param  n  the size of a real data sequence. When (<em>n</em>+1) is a multiplication of small
  * numbers (4, 2, 3, 5), this FFT transform is very efficient.
*/
     public RealDoubleFFT_Odd(int n)
     {
          ndim = n;
          norm_factor = 2*(n+1);
          int wtable_length = 2*ndim + ndim/2 + 3 + 15;
          if(wavetable == null || wavetable.length != wtable_length)
          {
              wavetable = new double[wtable_length];
          }
          sinti(ndim, wavetable);
     }
/**
  * Forward sine FFT transform. It computes the discrete sine transform of
  * an odd sequence.
  *
  * @param x an array which contains the sequence to be transformed. After FFT,
  * <em>x</em> contains the transform coeffients.
*/
     public void ft(double x[])
     {
         sint(ndim, x, wavetable);
     }
/**
  * Backward sine FFT transform. It is the unnormalized inverse transform of <em>ft</em>.
  *
  * @param x an array which contains the sequence to be transformed. After FFT,
  * <em>x</em> contains the transform coeffients.
*/
     public void bt(double x[])
     {
         sint(ndim, x, wavetable);
     }

/*---------------------------------------
   sint1: further processing of sine FFT.
  --------------------------------------*/

     void sint1(int n, double war[], double wtable[])
     {
          final double sqrt3=1.73205080756888;
          int     modn, i, k;
          double  xhold, t1, t2;
          int     kc, np1, ns2;
          int iw1, iw2, iw3;
          double[] wtable_p1 = new double[2*(n+1)+15];
          iw1=n / 2;
          iw2=iw1+n+1;
          iw3=iw2+n+1;

          double[] x = new double[n+1];

          for(i=0; i<n; i++)
          {
	      wtable[i+iw1]=war[i];
	      war[i]=wtable[i+iw2];
          }
          if(n<2)
          {
	      wtable[0+iw1]+=wtable[0+iw1];
          }
          else if(n==2)
          {
	      xhold=sqrt3*(wtable[0+iw1]+wtable[1+iw1]);
	      wtable[1+iw1]=sqrt3*(wtable[0+iw1]-wtable[1+iw1]);
	      wtable[0+iw1]=xhold;
          }
          else
          {
	      np1=n+1;
	      ns2=n / 2;
	      wtable[0+iw2]=0;
	      for(k=0; k<ns2; k++)
	      {
	          kc=n-k-1;
	          t1=wtable[k+iw1]-wtable[kc+iw1];
	          t2=wtable[k]*(wtable[k+iw1]+wtable[kc+iw1]);
	          wtable[k+1+iw2]=t1+t2;
	          wtable[kc+1+iw2]=t2-t1;
	      }
	      modn=n%2;
	      if(modn !=0)
	          wtable[ns2+1+iw2]=4*wtable[ns2+iw1];
              System.arraycopy(wtable, iw1, wtable_p1, 0, n+1);
              System.arraycopy(war, 0, wtable_p1, n+1, n);
              System.arraycopy(wtable, iw3, wtable_p1, 2*(n+1), 15);
              System.arraycopy(wtable, iw2, x, 0, n+1);
              rfftf1(np1, x, wtable_p1, 0);
              System.arraycopy(x, 0, wtable, iw2, n+1);
	      wtable[0+iw1]=0.5*wtable[0+iw2];
	      for(i=2; i<n; i+=2)
	      {
	          wtable[i-1+iw1]=-wtable[i+iw2];
	          wtable[i+iw1]=wtable[i-2+iw1]+wtable[i-1+iw2];
	      }
	      if(modn==0)
	          wtable[n-1+iw1]=-wtable[n+iw2];
          }
          for(i=0; i<n; i++)
          {
	      wtable[i+iw2]=war[i];
	      war[i]=wtable[i+iw1];
          }
     } 

/*----------------
   sint: sine FFT
  ---------------*/
     void sint(int n, double x[], double wtable[])
     {
          sint1(n, x, wtable);
     } 

/*----------------------------------------------------------------------
   sinti: initialization of sin-FFT
  ----------------------------------------------------------------------*/
     void sinti(int n, double wtable[])
     {
          final double pi=Math.PI; //3.14159265358979;
          int     k, ns2;
          double  dt;

          if(n<=1) return;
          ns2=n / 2;
          dt=pi /(double)(n+1);
          for(k=0; k<ns2; k++)
	      wtable[k]=2*Math.sin((k+1)*dt);
          rffti1(n+1, wtable, ns2);
     } 

}
