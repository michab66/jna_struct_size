package de.michab;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

/**
 * Demonstrates that the operation {@link Structure#size()} of a
 * compound structure like {@link CONTAINER} does not return
 * the proper size if the {@link Structure} contains a dynamic
 * size sub-structure.
 *
 * An example of a real-world usage of a compound structure can be found
 * here: https://github.com/microsoft/Windows-driver-samples/blob/d3ec258921cfcef7b053b5a2c866330d43dd3f03/usb/usbview/enum.c#L1917
 */
class BugDemo
{
    @FieldOrder(value = { "wSize", "buffer" })
    public static class DYNAMIC_STRUCT extends Structure
    {
        public DYNAMIC_STRUCT( int bufferSize ) {
            super( ALIGN_NONE );
            if ( bufferSize < 1 )
                throw new IllegalArgumentException( "size < 1" );
            wSize = bufferSize;
            buffer = new byte[ bufferSize ];
        }
        public DYNAMIC_STRUCT() {
            this( 1 );
        }

        public int wSize;
        public byte[] buffer;
    }

    @FieldOrder(value = { "dynamic" })
    public static class CONTAINER extends Structure
    {
        public CONTAINER( int cfgDescriptorSize ) {
            dynamic[0] = new DYNAMIC_STRUCT( cfgDescriptorSize );
            setAlignType( ALIGN_NONE );
        }
        public CONTAINER() {
            this( 1 );
        }

        // The trick is to place the dynamic sub-structure into
        // an array.  This does not change the structure physically,
        // but stops JNA from caching its size.
        public final DYNAMIC_STRUCT[] dynamic = new DYNAMIC_STRUCT[1];
    }

    private static void expect( int expected, int actual ) {
        if ( expected != actual )
            System.out.format( "Expected %d, got %d%n", expected, actual );
    }

    public static void main( String[] args )
    {
        DYNAMIC_STRUCT cd = new DYNAMIC_STRUCT( 1 );
        expect( 4+1, cd.size() );
        cd = new DYNAMIC_STRUCT( 10 );
        expect( 4+10, cd.size() );

        CONTAINER combined = new CONTAINER( 1 );
        expect( 4+1, combined.size() );

        combined = new CONTAINER( 10 );
        // Works now nicely as expected.
        expect( 4+10, combined.size() );

        combined = new CONTAINER( 313 );
        // Works now nicely as expected.
        expect( 4+313, combined.size() );
    }
}
