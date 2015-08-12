package ca.uol.aig.fftpack;
/**
  * cosine FFT transform of a real even sequence.
  * @author Baoshe Zhang
  * @author Astronomical Instrument Group of University of Lethbridge.
*/
public class RealDoubleFFT_Even extends RealDoubleFFT_Mixed
{
/**
  * <em>norm_factor</em> can be used to normalize this FFT transform. This is because
  * a call of forward transform (<em>ft</em>) followed by a call of backward transform
  * (<em>bt</em>) will multiply the input sequence by <em>norm_factor</em>.
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
  * @param  n  the size of a real data sequence. When (<em>n</em>-1) is a multiplication of small
  * numbers (4, 2, 3, 5), this FFT transform is very efficient.
*/
     public RealDoubleFFT_Even(int n)
     {
          ndim = n;
          norm_factor = 2*(n-1);
          if(wavetable == null || wavetable.length !=(3*ndim+15))
          {
              wavetable = new double[3*ndim + 15];
          }
          costi(ndim, wavetable);
     }

/**
  * Forward cosine FFT transform. It computes the discrete sine transform of
  * an odd sequence.
  *
  * @param x an array which contains the sequence to be transformed. After FFT,
  * <em>x</em> contains the transform coeffients.
*/
     public void ft(double x[])
     {
         cost(ndim, x, wavetable);
     }

/**
  * Backward cosine FFT transform. It is the unnormalized inverse transform of <em>ft</em>.
  *
  * @param x an array which contains the sequence to be transformed. After FFT,
  * <em>x</em> contains the transform coeffients.
*/
     public void bt(double x[])
     {
         cost(ndim, x, wavetable);
     }


/*-------------------------------------------------------------
   cost: cosine FFT. Backward and forward cos-FFT are the same.
  ------------------------------------------------------------*/
     void cost(int n, double x[], final double wtable[])
     {
          int     modn, i, k;
          double  c1, t1, t2;
          int     kc;
          double  xi;
          int     nm1;
          double  x1h;
          int     ns2;
          double  tx2, x1p3, xim2;


          nm1=n-1;
          ns2=n / 2;
          if(n-2<0) return;
          else if(n==2)
          {
	      x1h=x[0]+x[1];
	      x[1]=x[0]-x[1];
	      x[0]=x1h;
          }
          else if(n==3)
          {
	      x1p3=x[0]+x[2];
	      tx2=x[1]+x[1];
	      x[1]=x[0]-x[2];
	      x[0]=x1p3+tx2;
	      x[2]=x1p3-tx2;
          }
          else
          {
	      c1=x[0]-x[n-1];
	      x[0]+=x[n-1];
	      for(k=1; k<ns2; k++)
	      {
	          kc=nm1-k;
	          t1=x[k]+x[kc];
	          t2=x[k]-x[kc];
	          c1+=wtable[kc]*t2;
	          t2=wtable[k]*t2;
	          x[k]=t1-t2;
	          x[kc]=t1+t2;
	      }
	      modn=n%2;
	      if(modn !=0) x[ns2]+=x[ns2];
              rfftf1(nm1, x, wtable, n);
	      xim2=x[1];
	      x[1]=c1;
	      for(i=3; i<n; i+=2)
	      {
	          xi=x[i];
	          x[i]=x[i-2]-x[i-1];
	          x[i-1]=xim2;
	          xim2=xi;
	      }
	      if(modn !=0) x[n-1]=xim2;
         }
     } 

/*----------------------------------
   costi: initialization of cos-FFT
  ---------------------------------*/
     void costi(int n, double wtable[])
     {
          final double pi=Math.PI; //3.14159265358979;
          int     k, kc, ns2;
          double  dt;

          if(n<=3) return;
          ns2=n / 2;
          dt=pi /(double)(n-1);
          for(k=1; k<ns2; k++)
          {
	       kc=n-k-1;
	       wtable[k]=2*Math.sin(k*dt);
	       wtable[kc]=2*Math.cos(k*dt);
          }
          rffti1(n-1, wtable, n);
     }
}
