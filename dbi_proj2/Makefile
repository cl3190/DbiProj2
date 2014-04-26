all:	SplashTable probe
probe: probe.c
	gcc -o probe -msse2 -msse4 probe.c -lm
	chmod 777 probe
SplashTable: SplashTable.java
	javac SplashTable.java
	chmod 777 splash
clean:
	rm *.class probe