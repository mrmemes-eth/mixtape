# mixtape

Mixtape.JSON. You know it, you love it, now you can make programmatic changes to it.

## Preamble

This assumes a working Clojure 1.10 environment. Instructions for [installation of Clojure](https://clojure.org/guides/getting_started) are available for a variety of systems. Dependencies will be installed automatically when you use the `clojure` and/or `clj` CLI tools.

## Usage

When invoked without any arguments, the `mixtape.json` and `changes.json` files
in the `resources` directory will be used. You can specify alternate paths for
these files. Please invoke the application with `--help` or see "Options" below
for a full description. Assuming valid input files are used, an `output.json`
file is output in the same directory as the application is invoked from.

Run the project directly:

    $ clojure -m scaudill.mixtape

Run the project's tests:

    $ clojure -A:test:runner

Build an uberjar:

    $ clojure -A:uberjar

Run that uberjar:

    $ java -jar mixtape.jar

Pretty printing the JSON using python's readily available and fast printer:

    $ less output.json|python -m json.tool

## Options

**`-t` or `--mixtape`:**

This option can be used to specify an alternative path for `mixtape.json`,
otherwise `resources/mixtape.json` will be used.

**`-c` or `--changes`:**

This option can be used to specify an alternative path for `changes.json`,
otherwise `resources/changes.json` will be used.

**`-h` or `--help`:**

Shows option summary text that describes how to use this application.

## Examples

    $ clojure -m scaudill.mixtape --help
    $ clojure -m scaudill.mixtape ;; uses default file paths
    $ clojure -m scaudill.mixtape -t ../some/other/mixtape.json -c ../some/other/changes.json

## Thoughts on scaling

### First lets talk about hierarchical data

By employing a hierarchical data format like JSON, we gain rich modeling
capabilities that let us show deep interrelations between objects. Along with
that expressiveness comes the inherent drawback of needing to process the
entirety of an object to be able to see those interrelations. This is shown
aptly in `mixtape.json`, having it's three main sections, each having
relationships with the other. This has a direct impact on memory consumption: if
we must process the whole file to understand it, as the size of the file scales,
so must the size of the memory we hold it in. In the best case, it will require
O(1) space, but the likelihood is that it would be larger than that due to
intermediary data structures created in service of processing it.

This is not a novel problem for JSON. A number of IoT devices have attempted to
standardize "streaming JSON formats" and many more just ham-fistedly create
ad-hoc and poorly specified implementations all in pursuit of making their data
easy to access with the readily available tools for parsing JSON. There's a
business case to be made for doing just that, but unless this data format was
expected to be broadly used externally, I would argue for changing the data
format to something that is natively friendly to stream over the wire. I would
make the same argument for any JSON format that made use of large arrays close
to the top of the data structure... this is a tell tale sign that the object is
asking for a different representation.

The shape of the data lends itself to three separate streams. Both users and
songs lend themselves to a tabular data format. If I were optimizing for space
and memory consumption and not human readability, I'd stump for Apache's Avro
format as it's row-based and uses binary serialization. If human readability was
a priority, then CSV is, quite frankly, a nearly perfect fit: it's row-based,
easy to edit by hand and cheap to parse as a stream over TCP. Playlists offer a
slight complication, but serializing song_ids makes it effectively a non-issue.
With truly gargantuan playlist sizes, you would start to see issues again, but
making a logical limit to playlist size of e.g. 1024 songs would make it such
that even malicious actors couldn't cause problems. 1024 ints in memory is
trivial when dealing with playlists in a row-wise fashion.

### Changes to the program

Assuming we settled on some sort of data format that lent itself to streaming or
even if we decided JSON was a must have and we transformed it such that it was a
series of delimited objects, our next task would be improving the performance of
the program itself.

Taking advantage of java's buffered I/O streaming types seems like a good first
step, particularly if we decided to parse streaming JSON. We could eagerly
consume the stream until finding the delimiter before parsing. Attendant changes
to the parsing and manipulation of the data format would, of course, need to be
made. Further, there's a good argument for using `core.async`s CSP facilities to
parallelize the parsing, manipulation and unparsing. This same code could then
be scaled horizontally or vertically with very few changes.

## License

Copyright © 2020 Stephen Caudill

All rights reserved. Copying and modification are explicitly disallowed.
