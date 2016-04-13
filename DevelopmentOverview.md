# Introduction #

This is a learning project with an evolving design and isn't meant to be commercial-grade software. With that comes a "freedom to tinker" and try out new ideas and methods. That said, there are a few goals and guidelines to follow when developing the software: Performance and memory management.

# Programming Language #
Java SE 6

# Memory #
The datasets tend to be very large and produce huge images. The number of calculations required per model is huge. Also, the Java garbage collectors are only _so_ good at what they do, especially when processing the amount of data that the software is targeting. That said, efficient use of memory is extremely important.

As much as possible, memory reuse is used as the strategy of choice. Additionally, heavy usage of arrays are used for processing, as well as a more procedural design (as much as Java can do) as opposed to object-oriented design. This is due to the overhead placed on the allocation/deallocation of memory and the even higher overhead of object-oriented processing. By reducing the number of memory churning and administrative overhead, the efficiency algorithms can take over and drive overall performance.

# Development Environment #
The standard IDE is the vanilla Eclipse Helios with SVN support. However, official builds will be done via a build.xml Ant script.

# Third Party Libraries #
Because this is learning software, custom home-grown implementations of functionality is generally preferred over the use of 3rd party libraries. For example, the 3D rendering code has been implemented directly rather than relying on OpenGL or any other available product. Some have been brought in so far, and there is no absolute restriction on adding additional libraries in the future.