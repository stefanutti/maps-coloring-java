import argparse

parser = argparse.ArgumentParser()

group_input = parser.add_mutually_exclusive_group(required = True)
group_input.add_argument("-r", help = "Random graph with N faces", nargs = 1, type = int)
group_input.add_argument("-i", help = "Input filename", nargs = 1)
parser.add_argument("-o", help="Output filename", nargs = 1, required = False)

args = parser.parse_args()
print (args)

if args.i is None:
    print ("-o not given")

print (args.r[0])