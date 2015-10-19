package ca.uol.aig.fftpack;
/**
  * FFT transform of a complex periodic sequence.
  * @author Baoshe Zhang
  * @author Astronomical Instrument Group of University of Lethbridge.
*/
public class ComplexDoubleFFT extends ComplexDoubleFFT_Mixed
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
  * Construct a wavenumber table with size <em>n</em> for Complex FFT.
  * The sequences with the same size can share a wavenumber table. The prime
  * factorization of <em>n</em> together with a tabulation of the trigonometric functions
  * are computed and stored.
  *
  * @param  n  the size of a complex data sequence. When <em>n</em> is a multiplication of small
  * numbers (4, 2, 3, 5), this FFT transform is very efficient.
*/
     public ComplexDoubleFFT(int n)
     {
          ndim = n;
          norm_factor = n;
          if(wavetable == null || wavetable.length !=(4*ndim+15))
          {
              wavetable = new double[4*ndim + 15];
          }
          cffti(ndim, wavetable);
     }

/**
  * Forward complex FFT transform. 
  *
  * @param x  2*<em>n</em> real double data representing <em>n</em> complex double data.
  * As an input parameter, <em>x</em> is an array of 2*<em>n</em> real
  * data representing <em>n</em> complex data. As an output parameter, <em>x</em> represents <em>n</em>
  * FFT'd complex data. Their relation as follows:
  * <br>
  *  x[2*i] is the real part of <em>i</em>-th complex data;
  * <br>
  *  x[2*i+1] is the imaginary part of <em>i</em>-the complex data.
  *
*/
     public void ft(double x[])
     {
         if(x.length != 2*ndim) 
              throw new IllegalArgumentException("The length of data can not match that of the wavetable");
         cfftf(ndim, x, wavetable); 
     }

/**
  * Forward complex FFT transform.  
  *
  * @param x  an array of <em>n</em> Complex data
*/
     public void ft(Complex1D x)
     {
         if(x.x.length != ndim)
              throw new IllegalArgumentException("The length of data can not match that of the wavetable");
         double[] y = new double[2*ndim];
         for(int i=0; i<ndim; i++)
         {
              y[2*i] = x.x[i];
              y[2*i+1] = x.y[i];
         }
         cfftf(ndim, y, wavetable);
         for(int i=0; i<ndim; i++)
         {
              x.x[i]=y[2*i];
              x.y[i]=y[2*i+1];
         }
     }

/**
  * Backward complex FFT transform. It is the unnormalized inverse transform of <em>ft</em>(double[]).
  *
  * @param x  2*<em>n</em> real double data representing <em>n</em> complex double data.
  *
  * As an input parameter, <em>x</em> is an array of 2*<em>n</em>
  * real data representing <em>n</em> complex data. As an output parameter, <em>x</em> represents
  * <em>n</em> FFT'd complex data. Their relation as follows:
  * <br>
  *  x[2*<em>i</em>] is the real part of <em>i</em>-th complex data;
  * <br>
  *  x[2*<em>i</em>+1] is the imaginary part of <em>i</em>-the complex data.
  *
*/
     public void bt(double x[])
     {
         if(x.length != 2*ndim)
              throw new IllegalArgumentException("The length of data can not match that of the wavetable");
         cfftb(ndim, x, wavetable);
     }

/**
  * Backward complex FFT transform. It is the unnormalized inverse transform of <em>ft</em>(Complex1D[]). 
  *
  *
  * @param x  an array of <em>n</em> Complex data
*/
     public void bt(Complex1D x)
     {
         if(x.x.length != ndim)
              throw new IllegalArgumentException("The length of data can not match that of the wavetable");
         double[] y = new double[2*ndim];
         for(int i=0; i<ndim; i++)
         {
              y[2*i] = x.x[i];
              y[2*i+1] = x.y[i];
         }
         cfftb(ndim, y, wavetable);
         for(int i=0; i<ndim; i++)
         {
              x.x[i]=y[2*i];
              x.y[i]=y[2*i+1];
         }
     }
}
