import math
g = 5
p = 23
def calc(g, p, secret):
    return math.pow(g, secret) % p


print("type your secret key\n")
s = int(input())
temp = calc(g, p, s)
print("your share key: ", temp)
print("type your friend share key\n")
share_calc = int(input())
K = calc(share_calc, p, s)
print("Key = ", K)
print("any key to close app")
