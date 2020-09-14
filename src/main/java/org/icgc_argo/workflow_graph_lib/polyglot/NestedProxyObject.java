package org.icgc_argo.workflow_graph_lib.polyglot;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.util.Collection;
import java.util.Map;

// Workaround for: https://github.com/graalvm/graaljs/issues/79
// Original impl:
// https://github.com/oracle/graal/blob/34bbd8c6326d21523133bcdd00e822a7f69a9ee7/sdk/src/org.graalvm.polyglot/src/org/graalvm/polyglot/proxy/ProxyObject.java#L122
public class NestedProxyObject implements ProxyObject {

  Map<String, Object> values;

  public NestedProxyObject(Map<String, Object> values) {
    this.values = values;
  }

  @Override
  public Object getMember(String key) {
    Object v = values.get(key);
    return getProxyValue(v);
  }

  @Override
  public void putMember(String key, Value value) {
    values.put(key, value.isHostObject() ? value.asHostObject() : value);
  }

  @Override
  public boolean hasMember(String key) {
    return values.containsKey(key);
  }

  @Override
  public boolean removeMember(String key) {
    if (values.containsKey(key)) {
      values.remove(key);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public Object getMemberKeys() {
    return new NestedProxyArray(values.keySet().toArray());
  }

  private Object getProxyValue(Object v) {
    if (v instanceof Map) {
      return new NestedProxyObject((Map<String, Object>) v);
    } else if (v instanceof Object[]) {
      return new NestedProxyArray((Object[]) v);
    } else if (v instanceof Collection) {
      Collection<Object> vColl = (Collection<Object>) v;
      return new NestedProxyArray(vColl.toArray());
    } else {
      return v;
    }
  }

  public class NestedProxyArray implements ProxyArray {

    private Object[] values;

    public NestedProxyArray(Object[] v) {
      this.values = v;
    }

    @Override
    public void set(long index, Value value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long getSize() {
      return values.length;
    }

    @Override
    public Object get(long index) {
      if (index < 0 || index > Integer.MAX_VALUE) {
        throw new ArrayIndexOutOfBoundsException();
      }
      Object v = values[(int) index];
      return getProxyValue(v);
    }
  }
}
