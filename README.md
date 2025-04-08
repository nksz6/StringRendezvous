String Rendezvous Project - Simplified Instructions
by. Nick Kelley

Wanted to make the instructions a little easier
since there's a lot of information in the doc.

Formatted kind of weird like this,
but I like having the notepad open
and be able to read it in little
chunks... 

If anything is unclear,
you need to reference the doc.

Goal: Complete 'StringHandoffImpl' to fully implement
the behavior described in the interface: 'StringHandoff'.

There are 2 projects in the Zip:

(1)-	ProJ_ThreadRendezvous
	(PROJ_ThreadRendezvous)
		-StringHandoffImpl.java,
		-TestUltraBasic.java,
		-TestStringHandoff.java

(2)-	ProJ_ThreadRendezvous-Parallel-Common
	(Parallel-Common-PROJ-ThreadRendezvous)
	
StringHandoff.java + most of the tests.
		-Comments inside StringHandoff explain in detail
		the desired behavior. Read these fully.

--ONLY MODIFY THE StringHandoffImpl.java IN ProJ_ThreadRendezvous--

Plan of attack:
-Work on the tests TOP-DOWN, in succession.

-Before considering anything about timeouts, multiple
	passers, multiple passers, multiple receivers, shutdown,
	or any of the tests marked as advanced, design and code
	the most basic functionality.
	
-You can enhance your code later when you have the core
	part working.

-Every time you get a new chunk of code working, make a
	zip of your source folder, name the zip file something
	unique, and save it somewhere safe.

-USE GITHUB for this because you'll be able to
	go back if things get mangled.

-You should NOT need special classes beyond what
	we've used in class.

-You CANNOT use 'wait helper' utility for this
	project. You must write all the wait-notify code
	manually.

-The starting code for StringHandoffImpl.java
	has methods marked as 'synchronized'. Keep it that
	way. We're not using synchronized blocks for the 
	project.

-Have the methods that don't take a timeout call
	the ones that do passing in a timeout of 0L (zero)
	and keep these methods this way through your final
	solution:

(see doc for example of above.)

-FOR NOW, the methods that do take a timeout should be
	written to check for zero and IMMEDIATELY throw a
	RuntimeException if a non-zero timeout is passed in.

-I/We can fix this (above) later, but it allows us to create
	compliable and runnable code that can get the basic
	functionality working without any complexity
	trying to deal with timeouts:

(see doc for example of above.)

Run TestUltraBasic and get your code to work with it
	before moving onto the large test suite.

Until you get this (above) working, there's no
	point in adding any complexity.

-Run TestStringHandoff and notice the suite of tests which
	run in parallel. They run against separate instances of	
	your StringHandoffImpl class, so the parallel testing
	should not cause any interference.
  
    (a)- in general attack the tests in order.
  
    (b)- the "Points" column shows how many points
		your code is scoring for passing tests.

    (c)- ignore the "Valid" column.
	
    (d)- some of the "interrupt check" tests won't
		pass until non-zero timeouts are actually
		supported.

    (e)- get "single item pass and receive" to work
		first. This is worth a lot of points and the
		rest won't matter unless this works.

    (f)- next, get "multiple item pass and receive"
		to work. At this point, definitely zip up your src
		folder and put that zip file someplace safe.
		(commit to GitHub)

    (g)- then add support for timeouts and remove
		the throwing of a RuntimeException that you/we
		added earlier and confirm that all of the
		"interrupt check" tests now pass.
	
-Getting everything to work at this point puts your
	starting score at 84. This is all before you have
	even begun to consider multiple passers, multiple
	receivers, shutdown, or any of the tests marked as
	advanced!
		  
    (a)- at this point you should also zip your
		files again and save them somewhere safe.
		(make a GitHub commit again.)

	
-Once you get to the advanced tests, try them one at a
	time and remember they may be really difficult to
	implement and/or may require some significant design
	changes.


-See the Javadoc generated from the interface
	giving description of 'SringHandoff.java'.
  Read it all carefully, it fully explains how your code needs to
	work.

(Reference p.3 thru p.4 of doc for the rest, noy)
	
