# jna_struct_size

Demonstrates how a structure that contains dynamically sized sub-structures can be mapped properly in JNA.

The trick is to place the substructure into a single element array.  This does not change the physical in-memory layout of the structure but requires JNA to explicitly compute the actual size on each allocation.

Note that the original and recommended strategy using Structure#allocateMemory does *not* work for the general case.
