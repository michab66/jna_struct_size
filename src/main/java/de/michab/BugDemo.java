package de.michab;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

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
            dynamic = new DYNAMIC_STRUCT( cfgDescriptorSize );
            setAlignType( ALIGN_NONE );
        }
        public CONTAINER() {
            this( 1 );
        }

        public DYNAMIC_STRUCT dynamic;
    }

    private static void expect( int expected, int actual ) {
        if ( expected != actual )
            System.out.format( "Expected %d, got %d%n", expected, actual );
    }

    public static void main( String[] args )
    {
        {
            DYNAMIC_STRUCT cd = new DYNAMIC_STRUCT( 1 );
            expect( 4+1, cd.size() );
            cd = new DYNAMIC_STRUCT( 10 );
            expect( 4+10, cd.size() );
        }

        {
            CONTAINER combined = new CONTAINER( 1 );
            expect( 4+1, combined.size() );
            // This fails: 'Expected 14, got 5'
            combined = new CONTAINER( 10 );
            expect( 4+10, combined.size() );
            combined = new CONTAINER( 11 );
            expect( 4+11, combined.size() );
        }
    }
}
