# BULKINSERT utility for MSSql server

## Why
You might want to tell me that there already is a bulkinsert sql command for MS-Sql server but here is the problem I ran into.

I needed to populate a database that is running in a docker container (Linux) on a VM somewhere in the cloud. The bulk insert utility insisted that the data be on drive C:\ which, obviously, does not exist on Linux. It did seem to recognise the root of the filesystem as C:\ but, again obviously, I did not want to copy my data files into the root of the docker container. 

Bulkinsert is supposed to be able to work with a UNC, but with my local workstation also being Linux I just could not find a UNC that MS-Sql server could understand. Eventually I realised that I could have spent the time trying to make the bulkinsert work to write my own program. So I did.

In the meantime I did manage to use the bulkinsert to populate the database from an Azure blob. However, when I then tried to populate a database with some COVID data that was published on a government website I ran into the problem, as I guess you can expect from a government website, that the data was pretty messy. The problem with MS-Sql server's bulkinsert is that when it bombs out because of an error in the data you don't know where or why it happened. 

With my bulkinsert utility now written and with the help of OpenRefine, I was able to clean up the data and get it inserted into the database.

So now you know why I had to re-invent the wheel - or at least make one that can actually get the job done.

## How

This bulkinsert is written in Java and it is a command line utility.  

```
usage: java -cp Bulkinsert.jar view.BULKINSERT
Options shown with * are required.
 -b,--batch <arg>      Batch size (default=100)
 -d,--database <arg>   *Database
 -D,--delim <arg>      Delimiter (default=,)
 -f,--file <arg>       *CSV file to populate from
 -P,--password <arg>   *Password
 -p,--port <arg>       Port (default=1433)
 -s,--server <arg>     *Server
 -t,--table <arg>      *Table to be populated
 -U,--user <arg>       *Username
```

