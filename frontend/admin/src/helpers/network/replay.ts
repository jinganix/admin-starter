import { Deferred } from "@/helpers/network/deferred";

export class Replay<T> {
  private resolvedAt = 0;
  private deferred: Deferred<T> | null = null;
  key = "";

  constructor(value?: T | null) {
    if (value !== undefined && value !== null) {
      this.deferred = new Deferred<T>();
      this.deferred.resolve(value);
      this.resolvedAt = Date.now();
    }
  }

  async reset(): Promise<void> {
    this.deferred && (await this.deferred.promise);
    this.resolvedAt = 0;
    this.deferred = null;
    this.key = "";
  }

  private doResolve(value: T): void {
    this.resolvedAt = Date.now();
    this.deferred?.resolve(value);
  }

  private doReject(err: unknown): void {
    const deferred = this.deferred;
    this.deferred = null;
    deferred?.reject(err);
  }

  async resolve(defer: () => Promise<T>, key = ""): Promise<T> {
    if (!this.deferred || (this.key != key && this.resolved)) {
      this.key = key;
      const deferred = new Deferred<T>();
      this.deferred = deferred;
      this.resolvedAt = 0;
      try {
        this.doResolve(await defer());
      } catch (err) {
        this.doReject(err);
        return deferred.promise;
      }
    }
    return this.deferred.promise;
  }

  async value(): Promise<T | null> {
    return !this.deferred ? null : this.deferred.promise;
  }

  get resolved(): boolean {
    return this.resolvedAt > 0;
  }
}
