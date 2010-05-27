#!/usr/bin/perl

use WordNet::Similarity::vector;
use WordNet::Similarity::lesk;
use WordNet::Similarity::hso;
use WordNet::Similarity::lin;
use WordNet::QueryData;

local $| = 1;

# Perl trim function to remove whitespace from the start and end of the string
sub trim($)
{
	my $string = shift;
	$string =~ s/^\s+//;
	$string =~ s/\s+$//;
	return $string;
}

my $wn = WordNet::QueryData->new();
my $vector = WordNet::Similarity::vector->new($wn);
#my $vector = WordNet::Similarity::lesk->new($wn, 'lesk.conf');
#my $vector = WordNet::Similarity::lin->new($wn);
#my $vector = WordNet::Similarity::hso->new($wn);

for (;;) {
    undef $!;
    my $line = <>;
    
    if ($line) {
        my @ws = split(' ', trim($line));
        
        my $value = $vector->getRelatedness($ws[0], $ws[1]);
        ($error, $errorString) = $vector->getError();

        if($error) {
            print "0\n";
            #print "$errorString\n";
        }
        else {
            print "$value\n";
        }
    }
    else {
        last;
    }
}

exit(0);

# ca intrare primesc 2 termeni - sensul cuvantului 1 si pe cel al cuvantului 2
#my $word_sense1 = $ARGV[0];
#my $word_sense2 = $ARGV[1];

=pod
my $value = $vector->getRelatedness($word_sense1, $word_sense2);
($error, $errorString) = $vector->getError();

if($error) {
    print 0;
}
else {
    print "$value";
}
=cut

=pod
$value = $lesk->getRelatedness($word_sense1, $word_sense2);
($error, $errorString) = $lesk->getError();
if($error) {
    print 0;
}
else {
    print "$value";
}
=cut

=pod
$value = $lin->getRelatedness($word_sense1, $word_sense2);
($error, $errorString) = $lin->getError();
if($error) {
    print "$word_sense1 $word_sense2 0";
}
else {
    print "$word_sense1 $word_sense2 $value\n";
}
=cut

=pod
$value = $hso->getRelatedness($word_sense1, $word_sense2);
($error, $errorString) = $hso->getError();

if($error) {
    print 0;
}
else {
    print "$value";
}
=cut