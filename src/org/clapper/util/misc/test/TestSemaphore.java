/*---------------------------------------------------------------------------*\
  $Id$
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc.test;

import org.clapper.util.misc.Semaphore;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.clapper.util.cmdline.CommandLineUtility;
import org.clapper.util.cmdline.CommandLineException;
import org.clapper.util.cmdline.CommandLineUsageException;
import org.clapper.util.cmdline.UsageInfo;

/**
 *
 * @version <tt>$Revision$</tt>
 */
public class TestSemaphore extends CommandLineUtility
{
    private int        semCount = 0;
    private int        nThreads = 0;
    private int        holdTime = 0;
    private int        pendTime = 0;
    private Semaphore  semaphore = null;
    private Semaphore  parentSem = null;

    class TestThread extends Thread
    {
        TestThread (String name)
        {
            setName (name);
        }

        public void run()
        {
            boolean acquired = false;
            Semaphore semaphore = TestSemaphore.this.semaphore;
            Semaphore parentSem = TestSemaphore.this.parentSem;
            int holdTime = TestSemaphore.this.holdTime;
            int pendTime = TestSemaphore.this.pendTime;

            message ("Acquiring "
                   + semaphore.toString()
                   + ", pendTime = "
                   + pendTime);

            acquired = semaphore.acquire (pendTime);
            if (! acquired)
                message ("*** Failed to acquire semaphore.");

            else
            {
                // Synchronize on something else, to test deadlock.

                synchronized (TestSemaphore.class)
                {
                    message ("Got semaphore "
                           + semaphore
                           + ". Waiting "
                           + holdTime
                           + " milliseconds.");
                }

                try
                {
                    Thread.yield();
                    Thread.sleep (holdTime);
                }

                catch (InterruptedException ex)
                {
                }

                synchronized (TestSemaphore.class)
                {
                    message ("Releasing semaphore "
                             + semaphore
                             + ".");

                }

                semaphore.release();
            }

            message ("Notifying parent.");
            parentSem.release();
            message ("Exiting.");
        }

        private void message (String s)
        {
            TestSemaphore.this.message (s);
        }
    }

    public static void main (String args[])
    {
        TestSemaphore tester = new TestSemaphore();

        try
        {
            tester.execute (args);
        }

        catch (CommandLineUsageException ex)
        {
            // Already reported

            System.exit (1);
        }

        catch (CommandLineException ex)
        {
            System.err.println (ex.getMessage());
            ex.printStackTrace();
            System.exit (1);
        }

        catch (Exception ex)
        {
            ex.printStackTrace (System.err);
            System.exit (1);
        }
    }

    private TestSemaphore()
    {
        super();
    }

    protected void runCommand()
        throws CommandLineException
    {
        try
        {
            TestThread[] threads = new TestThread[nThreads];
            int          myPriority;
            int          i;

            this.semaphore = new Semaphore (semCount);
            this.parentSem = new Semaphore (0);

            myPriority = Thread.currentThread().getPriority();

            // Create and start the threads.

            for (i = 0; i < nThreads; i++)
            {
                String name = new String ("Thread-" + i);
                message ("Spawning thread " + name);
                threads[i] = new TestThread (name);
                threads[i].setPriority (Thread.MIN_PRIORITY);
            }

            for (i = 0; i < nThreads; i++)
            {
                message ( "Starting thread " + threads[i].getName());
                threads[i].start();
            }

            message ("Waiting for the child threads.");
            i = 0;
            while (i < nThreads)
            {
                parentSem.acquire();
                i++;
            }

            message ("Checking final count on semaphore.");
            int val = semaphore.getValue();

            if (val != semCount)
            {
                throw new Exception ("Count mismatch: Value is "
                                   + val
                                   + ", expected "
                                   + semCount);
            }

            message ("OK");
        }

        catch (Exception ex)
        {
            throw new CommandLineException (ex);
        }
    }

    protected void parseCustomOption (char     shortOption,
                                      String   longOption,
                                      Iterator it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        throw new IllegalStateException ("(BUG) Unknown option: "
                                       + shortOption);
    }
    
    protected void processPostOptionCommandLine (Iterator it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        semCount = parseIntParam ((String) it.next());
        nThreads = parseIntParam ((String) it.next());
        holdTime = parseIntParam ((String) it.next());
        pendTime = parseIntParam ((String) it.next());
    }

    protected void getCustomUsageInfo (UsageInfo info)
    {
        info.addParameter ("semCount",
                           "Initial value of semaphore",
                           true);
        info.addParameter ("nThreads",
                           "Number of threads to spawn",
                           true);
        info.addParameter ("holdTime",
                           "How long, in milliseconds, a thread should hold "
                         + "a semaphore.",
                           true);
        info.addParameter ("pendTime",
                           "How long, in milliseconds, a thread should wait "
                         + "to acquire a semaphore. 0 means forever.",
                           true);
    }

    private int parseIntParam (String value)
        throws CommandLineUsageException
    {
        try
        {
            return Integer.parseInt (value);
        }

        catch (NumberFormatException ex)
        {
            throw new CommandLineUsageException ("bad numeric parameter: \""
                                               + value
                                               + "\"");
        }
    }

    /**
     * Display a message, atomically.
     *
     * @param s  Message to display
     */
    private synchronized void message (String s)
    {
        SimpleDateFormat fmt = new SimpleDateFormat ("hh:mm:ss");
        System.out.println ( fmt.format (new Date())
                           + " ("
                           + Thread.currentThread().getName()
                           + ") "
                           + s);
    }
}
