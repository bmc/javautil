/*---------------------------------------------------------------------------*\
  $Id$
  ---------------------------------------------------------------------------
  This software is released under a Berkeley-style license:

  Copyright (c) 2004-2007 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms are permitted provided
  that: (1) source distributions retain this entire copyright notice and
  comment; and (2) modifications made to the software are prominently
  mentioned, and a copy of the original software (or a pointer to its
  location) are included. The name of the author may not be used to endorse
  or promote products derived from this software without specific prior
  written permission.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
  WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.

  Effectively, this means you can do what you want with the software except
  remove this notice or take advantage of the author's name. If you modify
  the software and redistribute your modified version, you must indicate that
  your version is a modification of the original, and you must provide either
  a pointer to or a copy of the original.
\*---------------------------------------------------------------------------*/

package org.clapper.util.misc.test;

import org.clapper.util.misc.ObjectLockSemaphore;
import org.clapper.util.misc.Semaphore;
import org.clapper.util.misc.SemaphoreException;

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
            try
            {
                doTest();
            }

            catch (SemaphoreException ex)
            {
                message ("*** semaphore error: " + ex.toString());
            }
        }

        private void doTest()
            throws SemaphoreException
        {
            boolean acquired = false;
            Semaphore semaphore = TestSemaphore.this.semaphore;
            Semaphore parentSem = TestSemaphore.this.parentSem;
            int holdTime = TestSemaphore.this.holdTime;
            int pendTime = TestSemaphore.this.pendTime;

            message ("Acquiring " + semaphore.toString() +
                     ", pendTime = " + pendTime);

            acquired = semaphore.acquire (pendTime);
            if (! acquired)
                message ("*** Failed to acquire semaphore.");

            else
            {
                // Synchronize on something else, to test deadlock.

                synchronized (TestSemaphore.class)
                {
                    message ("Got semaphore " + semaphore + ". Waiting " +
                             holdTime + " milliseconds.");
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
                    message ("Releasing semaphore " + semaphore + ".");

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

            this.semaphore = new ObjectLockSemaphore (semCount);
            this.parentSem = new ObjectLockSemaphore (0);

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
                throw new Exception ("Count mismatch: Value is " + val +
                                     ", expected " + semCount);
            }

            message ("OK");
        }

        catch (Exception ex)
        {
            throw new CommandLineException (ex);
        }
    }

    protected void parseCustomOption (char             shortOption,
                                      String           longOption,
                                      Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        throw new IllegalStateException ("(BUG) Unknown option: " +
                                         shortOption);
    }
    
    protected void processPostOptionCommandLine (Iterator<String> it)
        throws CommandLineUsageException,
               NoSuchElementException
    {
        semCount = parseIntParameter (it.next());
        nThreads = parseIntParameter (it.next());
        holdTime = parseIntParameter (it.next());
        pendTime = parseIntParameter (it.next());
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
                           "How long, in milliseconds, a thread should hold " +
                           "a semaphore.",
                           true);
        info.addParameter ("pendTime",
                           "How long, in milliseconds, a thread should wait " +
                           "to acquire a semaphore. 0 means forever.",
                           true);
    }

    /**
     * Display a message, atomically.
     *
     * @param s  Message to display
     */
    private synchronized void message (String s)
    {
        SimpleDateFormat fmt = new SimpleDateFormat ("hh:mm:ss");
        System.out.println ( fmt.format (new Date()) +
                             " (" +
                             Thread.currentThread().getName() +
                             ") " +
                             s);
    }
}
